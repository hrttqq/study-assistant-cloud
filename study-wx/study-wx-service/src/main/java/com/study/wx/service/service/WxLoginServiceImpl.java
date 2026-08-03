package com.study.wx.service.service;

import com.alibaba.fastjson.JSONObject;
import com.study.common.core.model.ResultMode;
import com.study.common.core.utils.RedisLockUtil;
import com.study.common.core.utils.RestInterface;
import com.study.wx.api.dto.WeChatAppLoginUserDTO;
import com.study.wx.api.dto.WeChatSilenceLoginDTO;
import com.study.wx.api.service.WxLoginService;
import com.study.wx.api.vo.WxUserLoginResponseVO;
import com.study.wx.service.domain.WeChatAccessTokenResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.study.wx.service.config.WxMiniProgramProperties;

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
