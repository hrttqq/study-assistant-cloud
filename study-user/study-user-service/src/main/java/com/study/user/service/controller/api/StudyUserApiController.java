package com.study.user.service.controller.api;


import com.study.user.api.dto.StudyWxUserRegisterDTO;
import com.study.user.api.response.StudyUserResponse;
import com.study.user.api.service.StudyUserService;
import com.study.user.api.service.StudyWxUserService;
import com.study.user.api.response.StudyWxUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/studyUser")
public class StudyUserApiController {

    @Autowired
    private StudyWxUserService studyWxUserService;
    @Autowired
    private StudyUserService studyUserService;

    @RequestMapping(value = "/wx/getOpenId", method = RequestMethod.POST)
    public StudyWxUserResponse getOpenId(@RequestParam("openId") String openId,
                                         @RequestParam(value = "wxUserStatus", required = false) String wxUserStatus) {
        return studyWxUserService.getOpenId(openId, wxUserStatus);
    }

    @RequestMapping(value = "/wx/createStudyWxUser", method = RequestMethod.POST)
    public StudyWxUserResponse createStudyWxUser(@RequestBody StudyWxUserRegisterDTO wxUserRegisterDTO) {
        return studyWxUserService.createStudyWxUser(wxUserRegisterDTO);
    }

    @PostMapping("/wx/updateWxInfoAfter")
    public void updateWxInfoAfter(@RequestParam("mobile") String mobile,
                                  @RequestParam("wxUserId") String wxUserId) {
        studyWxUserService.updateWxInfoAfter(mobile, wxUserId);
    }

    @PostMapping("/getModelByLoginId")
    public StudyUserResponse getModelByLoginId(@RequestParam("studyUserId") String studyUserId) {
        return studyUserService.getModelByLoginId(studyUserId);
    }

}
