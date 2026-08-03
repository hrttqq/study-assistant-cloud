package com.study.wx.api.service;

import com.study.common.core.model.ResultMode;
import com.study.wx.api.dto.WeChatAppLoginUserDTO;
import com.study.wx.api.dto.WeChatSilenceLoginDTO;
import com.study.wx.api.vo.WxUserLoginResponseVO;

public interface WxLoginService {

    /**
     * 一键登录
     *
     * @param request
     * @return
     */
    ResultMode<WxUserLoginResponseVO> waChatAppleLogin(WeChatAppLoginUserDTO request);

    /**
     * 静默登录
     *
     * @param request
     * @return
     */
    ResultMode<WxUserLoginResponseVO> silentLogin(WeChatSilenceLoginDTO request);
}
