package com.study.wx.api.feign;


import com.study.user.api.response.StudyUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用权限服务模块feign
 */
@FeignClient(name = "study-auth-service", url = "#{'${studyAuth.host}'}")
public interface StudyAuthApiService {
    @PostMapping("/api/studyAuth/token/create")
    String getJwtToken(@RequestBody StudyUserResponse studyUser,
                       @RequestParam(value = "token", required = false) String token);
}
