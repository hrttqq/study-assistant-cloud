package com.study.auth.service.controller.api;

import com.study.auth.api.service.StudyAuthService;
import com.study.user.api.response.StudyUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/studyAuth")
public class StudyAuthApiController {

    @Autowired
    private StudyAuthService studyAuthService;


    @PostMapping("/token/create")
    String getJwtToken(@RequestBody StudyUserResponse studyUser,
                       @RequestParam(value = "clientType", required = false) String clientType){
        return studyAuthService.getJwtToken(studyUser,clientType);
    }
}
