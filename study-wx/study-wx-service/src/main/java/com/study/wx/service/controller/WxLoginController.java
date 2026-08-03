package com.study.wx.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.enumconstants.ErrorCodeEnum;
import com.study.common.core.model.ResultMode;
import com.study.wx.api.dto.WeChatAppLoginUserDTO;
import com.study.wx.api.dto.WeChatSilenceLoginDTO;
import com.study.wx.api.inter.WxLoginApi;
import com.study.wx.api.service.WxLoginService;
import com.study.wx.api.vo.WxUserLoginResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx")
public class WxLoginController implements WxLoginApi {

    @Autowired
    private WxLoginService wxLoginService;

    /**
     * 探活接口
     *
     * @return
     */
    @Override
    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.WX);
        result.put("status", "UP");
        return resultMode;
        //return ResultMode(Map.of("service", ServiceNames.WX, "status", "UP"));
    }

    /**
     * 一键登录
     *
     * @param request
     * @return
     */
    @Override
    @PostMapping("/login")
    public ResultMode<WxUserLoginResponseVO> oneClickLogin(@RequestBody WeChatAppLoginUserDTO request) {
        ResultMode<WxUserLoginResponseVO> resultMode = new ResultMode<>();
        if (!StringUtils.isNotBlank(request.getCode())) {
            resultMode.setErrMsg("code:" + "不能为空字符");
            resultMode.setSucceed(false);
            return resultMode;
        }

        if (!resultMode.getSucceed()) {
            resultMode.setErrorCodeEnum(ErrorCodeEnum.PARAM_CHECK);
            return resultMode;
        }

        return wxLoginService.waChatAppleLogin(request);
    }

    /**
     * 静默登录
     *
     * @param request
     * @return
     */
    @Override
    @PostMapping("/silent-login")
    public ResultMode<WxUserLoginResponseVO> silentLogin(@RequestBody WeChatSilenceLoginDTO request) {
        ResultMode<String> resultMode = new ResultMode<>();
        if (!StringUtils.isNotBlank(request.getCode())) {
            resultMode.getModel().add("code:" + "不能为空字符");
            resultMode.setSucceed(false);
        }
        if (!resultMode.getSucceed()) {
            resultMode.setErrCode(ErrorCodeEnum.PARAM_CHECK.getCode());
            resultMode.setErrMsg(ErrorCodeEnum.PARAM_CHECK.getDesc());
            resultMode.setSucceed(false);
        }

        return wxLoginService.silentLogin(request);
    }
}
