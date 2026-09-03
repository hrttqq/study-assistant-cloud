package com.study.wx.service.feign;


import com.study.user.api.dto.StudyWxUserRegisterDTO;
import com.study.user.api.response.StudyUserResponse;
import com.study.user.api.response.StudyWxUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "study-user-service", url = "#{'${studyWxUser.host}'}")
public interface StudyUserApiService {

    @PostMapping("/api/studyUser/wx/getOpenId")
    StudyWxUserResponse getOpenId(@RequestParam("openId") String openId,
                                  @RequestParam(value = "wxUserStatus", required = false) String wxUserStatus);

    @PostMapping("/api/studyUser/wx/createStudyWxUser")
    StudyWxUserResponse createStudyWxUser(@RequestBody StudyWxUserRegisterDTO wxUserRegisterDTO);

    @PostMapping("/api/studyUser/wx/updateWxInfoAfter")
    void updateWxInfoAfter(@RequestParam("mobile") String mobile,
                           @RequestParam("wxUserId") String wxUserId);

    @PostMapping("/api/studyUser/getModelByLoginId")
    StudyUserResponse getModelByLoginId(@RequestParam("studyUserId") String studyUserId);
}
