package com.study.user.service.controller.api;


import com.study.user.api.dto.StudyWxUserRegisterDTO;
import com.study.user.api.service.StudyWxUserService;
import com.study.user.api.response.StudyWxUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/studyUser")
public class StudyWxUserApiController {

    @Autowired
    private StudyWxUserService studyWxUserService;

    @RequestMapping(value = "/wx/getOpenId", method = RequestMethod.POST)
    public StudyWxUserResponse getOpenId(String openId, String wxUserStatus) {
        return studyWxUserService.getOpenId(openId, wxUserStatus);
    }

    @RequestMapping(value = "/wx/createStudyWxUser", method = RequestMethod.POST)
    public StudyWxUserResponse createStudyWxUser(StudyWxUserRegisterDTO wxUserRegisterDTO) {
        return studyWxUserService.createStudyWxUser(wxUserRegisterDTO);
    }


}
