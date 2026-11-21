package com.study.file.service.controller.api;


import com.study.file.api.dto.FileRelationRequest;
import com.study.file.api.response.StudyFileResponse;
import com.study.file.api.service.StudyFileService;
import com.study.file.api.vo.StudyFileDownloadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyFile")
public class StudyFileApiController {

    @Autowired
    private StudyFileService studyFileService;


    /**
     * 根据用户ID和文件ID查询文件
     */
    @GetMapping("/getFile")
    public StudyFileResponse getFile(
            @RequestParam String studyUserId,
            @RequestParam Long fileId) {

        return studyFileService.getFile(
                studyUserId, fileId);
    }

    /**
     * 查询业务关联文件
     */
    @GetMapping("/listByBusiness")
    public List<StudyFileResponse> listByBusiness(
            @RequestParam String studyUserId,
            @RequestParam String businessType,
            @RequestParam Long businessId) {

        return studyFileService.listByBusiness(
                studyUserId,
                businessType,
                businessId);
    }

    /**
     * 获取文件临时下载地址
     */
    @GetMapping("/getDownloadUrl")
    public StudyFileDownloadVO getDownloadUrl(
            @RequestParam String studyUserId,
            @RequestParam Long fileId) {

        return studyFileService.getDownloadUrl(
                studyUserId, fileId);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/deleteFile")
    public void deleteFile(
            @RequestParam String studyUserId,
            @RequestParam Long fileId) {

        studyFileService.deleteFile(
                studyUserId, fileId);
    }

    @PostMapping("/bindRelation")
    public void bindRelation(
            @RequestParam String studyUserId,
            @RequestBody FileRelationRequest request) {

        studyFileService.bindRelation(
                studyUserId, request);
    }
}
