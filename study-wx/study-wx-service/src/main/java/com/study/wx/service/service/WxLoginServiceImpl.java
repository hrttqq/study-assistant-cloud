package com.study.wx.service.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.study.common.core.constant.StudyConstant;
import com.study.common.core.model.ResultMode;
import com.study.common.core.utils.HttpHelpUtil;
import com.study.common.core.utils.RedisLockUtil;
import com.study.common.core.utils.RestInterface;
import com.study.wx.api.dto.WeChatAppLoginUserDTO;
import com.study.wx.api.dto.WeChatSilenceLoginDTO;
import com.study.wx.api.service.WxLoginService;
import com.study.wx.api.vo.WxUserLoginResponseVO;
import com.study.wx.service.domain.WeChatUserTokenResponseVO;
import com.study.wx.service.domain.WeChatAccessTokenResponseVO;
import com.study.wx.service.utils.WxInteractionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.study.wx.service.config.WxMiniProgramProperties;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WxLoginServiceImpl implements WxLoginService {

    @Autowired
    private WxMiniProgramProperties wxProperties;

    @Autowired
    private RestInterface restInterface;


    private static final String WECHAT_LOGIN_KEY = "wechat_login_key";


    /**
     * 微信小程序登录
     *
     * @param request
     * @return
     */
    @Override
    public ResultMode<WxUserLoginResponseVO> waChatAppleLogin(WeChatAppLoginUserDTO request) {
        log.info("WeChatLoginBusiness_waChatAppleLogin_request:{}", JSONObject.toJSON(request));
        ResultMode<WxUserLoginResponseVO> resultMode = new ResultMode<>();

        //1、获取session_key
        WeChatAccessTokenResponseVO accessTokenResponse = getTokenResponse(request.getCode());
        log.info("WeChatLoginBusiness_waChatAppleLogin_accessTokenResponse:{}", JSONObject.toJSON(accessTokenResponse));
        if (null == accessTokenResponse || StringUtils.isBlank(accessTokenResponse.getSession_key())) {
            resultMode.setSucceed(false);
            resultMode.setErrMsg("微信授权获取微信信息异常");
            return resultMode;
        }
        //2、幂等校验
        boolean flag = RedisLockUtil.tryGetDistributedLock(WECHAT_LOGIN_KEY + accessTokenResponse.getOpenid(), WECHAT_LOGIN_KEY + accessTokenResponse.getOpenid(), 3000);
        //checkRepeatedSubmission(accessTokenResponse.getOpenid());
        if (!flag) {
            resultMode.setSucceed(false);
            resultMode.setErrMsg("请勿重复操作");
            return resultMode;
        }

        //3、手机号解析
        String mobile = null;
        if (StringUtils.isNotBlank(request.getMobileCode())) {
            mobile = getMobile(request.getMobileCode());
            if (StringUtils.isBlank(mobile)) {
                resultMode.setSucceed(false);
                resultMode.setErrMsg("微信授权解析数据异常");
                return resultMode;
            }
        }
        //4、后置处理
        resultMode = loginAfter(accessTokenResponse, mobile);
        return resultMode;
    }

    /**
     * 解析手机号
     *
     * @return java.lang.String
     * @author gq
     * @date 2022/6/22 14:58
     * @Param: mobileCode
     */
    private String getMobile(String mobileCode) {
        return getMobile(mobileCode, false);
    }

    /**
     * 解析手机号 新增重试机制限制
     * @param mobileCode
     * @param isRetry
     * @return
     */
    private String getMobile(String mobileCode, boolean isRetry) {
        log.info("WeChatLoginBusiness_getMobile_mobileCode：{}, isRetry：{}", mobileCode, isRetry);
        String mobile = null;
        try {
            String token = WxInteractionUtil.getAccessToken(wxProperties.getAppId(), wxProperties.getAppSecret(), restInterface);
            if (StringUtils.isNotBlank(token)) {
                String url = String.format(StudyConstant.WECHAT_PHONE_NUMBER, token);
                Map<String, String> map = new HashMap<>();
                map.put("code", mobileCode);
                String result = restInterface.postFormV3(url, map, HttpHelpUtil.getHeadersAuth(MediaType.APPLICATION_JSON, new HashMap<>()));

                log.info("WeChatLoginBusiness_getMobile_token:{}, requestUrl:{}, responseResult:{}", token, url, JSON.toJSONString(result));

                JSONObject resultJson = JSONObject.parseObject(result);
                String errcode = resultJson.getString("errcode");
                String errmsg = resultJson.getString("errmsg");

                if (StringUtils.isNotBlank(errcode) && errcode.equals("0")) {
                    WeChatUserTokenResponseVO chatUserInfo = HttpHelpUtil.jsonToObj(resultJson.getString("phone_info"), WeChatUserTokenResponseVO.class);
                    if (null != chatUserInfo) {
                        mobile = chatUserInfo.getPurePhoneNumber();
                    }
                } else if ("40001".equals(errcode) && !isRetry) {
                    // 仅在 token 失效且非重试时，使用refreshAccessToken刷新token并重试一次
                    log.info("微信access_token失效(errcode=40001)，刷新token并重试");
                    String newToken = WxInteractionUtil.refreshAccessToken(wxProperties.getAppId(), wxProperties.getAppSecret(), restInterface);
                    if (StringUtils.isNotBlank(newToken)) {
                        String retryUrl = String.format(StudyConstant.WECHAT_PHONE_NUMBER, newToken);
                        String retryResult = restInterface.postFormV3(retryUrl, map, HttpHelpUtil.getHeadersAuth(MediaType.APPLICATION_JSON, new HashMap<>()));
                        log.info("WeChatLoginBusiness_getMobile_retry_token:{}, requestUrl:{}, responseResult:{}", newToken, retryUrl, JSON.toJSONString(retryResult));
                        JSONObject retryResultJson = JSONObject.parseObject(retryResult);
                        String retryErrcode = retryResultJson.getString("errcode");
                        if (StringUtils.isNotBlank(retryErrcode) && retryErrcode.equals("0")) {
                            WeChatUserTokenResponseVO chatUserInfo = HttpHelpUtil.jsonToObj(retryResultJson.getString("phone_info"), WeChatUserTokenResponseVO.class);
                            if (null != chatUserInfo) {
                                mobile = chatUserInfo.getPurePhoneNumber();
                            }
                        } else {
                            log.info("微信手机号接口重试返回错误，errcode:{}, errmsg:{}", retryErrcode, retryResultJson.getString("errmsg"));
                        }
                    } else {
                        log.info("刷新token失败，newToken为空");
                    }
                } else {
                    // 其他错误码或已重试过，记录日志但不刷新缓存
                    log.info("微信手机号接口返回错误，errcode:{}, errmsg:{}, isRetry:{}", errcode, errmsg, isRetry);
                }
            } else {
                log.info("获取access_token失败，token为空");
            }
        } catch (Exception e) {
            log.error("解析授权手机号异常", e);
        }
        return mobile;
    }

    /**
     * 获取授权token
     *
     * @return com.mz.hy.ssoams.api.mvcvo.WeChatAccessTokenResponseVO
     * @throws
     * @author gq
     * @date 2022/5/25 11:05
     * @Param: code
     */
    private WeChatAccessTokenResponseVO getTokenResponse(String code) {
        String url = String.format(wxProperties.getCode2SessionUrl(), wxProperties.getAppId(), wxProperties.getAppSecret(), code);
        log.info("WeChatLoginBusiness_getTokenResponse_url=" + url + ",code=" + code);
        String result = restInterface.get(url);
        log.info("--------------------------" + result);
        return HttpHelpUtil.jsonToObj(result, WeChatAccessTokenResponseVO.class);
    }

    private ResultMode<WxUserLoginResponseVO> loginAfter(WeChatAccessTokenResponseVO accessTokenResponse, String mobile) {
        return null;
    }

    @Override
    public ResultMode<WxUserLoginResponseVO> silentLogin(WeChatSilenceLoginDTO request) {
        return null;
    }
}
