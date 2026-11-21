package com.study.file.api.service;

import com.study.file.api.dto.FileRelationRequest;
import com.study.file.api.response.StudyFileResponse;
import com.study.file.api.vo.StudyFileDownloadVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudyFileService {

    /**
     * 上传文件
     */
    StudyFileResponse upload(String studyUserId, MultipartFile file);

    /**
     * 根据文件ID查询
     */
    StudyFileResponse getFile(String studyUserId, Long fileId);

    /**
     * 根据业务对象查询文件
     */
    List<StudyFileResponse> listByBusiness(String studyUserId, String businessType, Long businessId);

    /**
     * 获取文件临时下载地址
     */
    StudyFileDownloadVO getDownloadUrl(String studyUserId, Long fileId);

    /**
     * 删除文件
     */
    void deleteFile(String studyUserId, Long fileId);

    /**
     * 关联文件与业务对象
     */
    void bindRelation(String studyUserId, FileRelationRequest request);
}
