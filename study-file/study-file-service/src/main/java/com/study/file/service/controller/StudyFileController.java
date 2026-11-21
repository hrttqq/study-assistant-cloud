package com.study.file.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.common.core.context.UserContext;
import com.study.common.core.model.ResultMode;
import com.study.file.api.response.StudyFileResponse;
import com.study.file.api.service.StudyFileService;
import com.study.file.api.vo.StudyFileDownloadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class StudyFileController {

    @Autowired
    private StudyFileService studyFileService;


    @GetMapping("/health")
    public ResultMode<Map<String, String>> health() {
        ResultMode<Map<String, String>> resultMode = new ResultMode<>();
        Map<String, String> result = new HashMap<>();
        result.put("service", ServiceNamesConstant.FILE);
        result.put("status", "UP");
        resultMode.setData(result);
        return resultMode;
    }

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public ResultMode<StudyFileResponse> upload(
            @RequestParam("file") MultipartFile file) {

        return wrap(
                studyFileService.upload(
                        currentUserId(), file));
    }

    /**
     * 查询文件详情
     */
    @GetMapping("/{fileId}")
    public ResultMode<StudyFileResponse> getFile(
            @PathVariable Long fileId) {

        return wrap(
                studyFileService.getFile(
                        currentUserId(), fileId));
    }

    /**
     * 查询业务关联文件
     */
    @GetMapping("/list")
    public ResultMode<List<StudyFileResponse>> list(
            @RequestParam String businessType,
            @RequestParam Long businessId) {

        return wrap(
                studyFileService.listByBusiness(
                        currentUserId(),
                        businessType,
                        businessId));
    }

    /**
     * 获取临时下载地址
     */
    @GetMapping("/{fileId}/download")
    public ResultMode<StudyFileDownloadVO> download(
            @PathVariable Long fileId) {

        return wrap(
                studyFileService.getDownloadUrl(
                        currentUserId(), fileId));
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public ResultMode<Void> delete(
            @PathVariable Long fileId) {

        studyFileService.deleteFile(
                currentUserId(), fileId);

        return wrap(null);
    }

    private String currentUserId() {

        String userId = UserContext.getUserId();

        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "用户未登录");
        }

        return userId;
    }

    private <T> ResultMode<T> wrap(T data) {

        ResultMode<T> result = new ResultMode<>();
        result.setData(data);

        return result;
    }

}
