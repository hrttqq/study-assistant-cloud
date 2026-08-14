package com.study.wx.service.feign;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "study-auth-service", url = "#{'${studyAuth.host}'}")
public interface StudyAuthApiService {
}
