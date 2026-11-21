package com.study.file.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.file.api.dto.FileRelationRequest;
import com.study.file.api.entity.StudyFileEntity;
import com.study.file.api.entity.StudyFileRelationEntity;
import com.study.file.api.response.StudyFileResponse;
import com.study.file.api.service.StudyFileService;
import com.study.file.api.vo.StudyFileDownloadVO;
import com.study.file.service.config.QiniuProperties;
import com.study.file.service.mapper.StudyFileMapper;
import com.study.file.service.mapper.StudyFileRelationMapper;
import com.study.file.service.storage.StorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class StudyFileServiceImpl implements StudyFileService {

    @Autowired
    private StudyFileMapper studyFileMapper;

    @Autowired
    private StorageProvider storageProvider;

    @Autowired
    private QiniuProperties qiniuProperties;

    @Autowired
    private StudyFileRelationMapper relationMapper;


    /**
     * 上传文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudyFileResponse upload(
            String studyUserId,
            MultipartFile file) {

        requireUser(studyUserId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        if (file.getSize() > qiniuProperties.getMaxFileSize()) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "文件大小超过限制");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("文件名称不能为空");
        }

        String extension = getExtension(originalName);

        // 后端生成唯一 Key，避免用户覆盖其他文件
        String fileKey = generateFileKey(studyUserId, extension);

        boolean uploaded = false;

        try (var inputStream = file.getInputStream()) {
            storageProvider.upload(
                    fileKey,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType());

            uploaded = true;

            StudyFileEntity entity = new StudyFileEntity();

            entity.setStudyUserId(studyUserId);
            entity.setOriginalName(originalName);
            entity.setFileKey(fileKey);
            entity.setBucketName(qiniuProperties.getBucket());
            entity.setStorageProvider(storageProvider.providerCode());
            entity.setExtension(extension);
            entity.setContentType(file.getContentType());
            entity.setFileSize(file.getSize());
            entity.setStatus(1);
            entity.setDeleted(0);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());

            studyFileMapper.insert(entity);

            return toResponse(entity);

        } catch (Exception e) {

            // 数据库保存失败时，补偿删除已经上传的对象
            if (uploaded) {
                try {
                    storageProvider.delete(fileKey);
                } catch (Exception deleteException) {
                    log.error(
                            "文件上传失败后的云端补偿删除失败，fileKey={}",
                            fileKey,
                            deleteException);
                }
            }

            throw new IllegalStateException("文件上传失败", e);
        }
    }

    /**
     * 根据文件ID查询
     */
    @Override
    public StudyFileResponse getFile(
            String studyUserId,
            Long fileId) {

        StudyFileEntity entity =
                requireOwnedFile(studyUserId, fileId);

        return toResponse(entity);
    }

    /**
     * 查询业务关联文件
     *
     * 当前版本先预留业务关联查询，
     * 后续接入 StudyFileRelationMapper。
     */
    @Override
    public List<StudyFileResponse> listByBusiness(
            String studyUserId,
            String businessType,
            Long businessId) {

        requireUser(studyUserId);

        if (businessType == null || businessType.isBlank()
                || businessId == null) {
            throw new IllegalArgumentException("业务参数不能为空");
        }

        List<StudyFileRelationEntity> relations =
                relationMapper.selectList(
                        new LambdaQueryWrapper<StudyFileRelationEntity>()
                                .eq(StudyFileRelationEntity::getStudyUserId, studyUserId)
                                .eq(StudyFileRelationEntity::getBusinessType, businessType)
                                .eq(StudyFileRelationEntity::getBusinessId, businessId));

        if (relations.isEmpty()) {
            return List.of();
        }

        List<Long> fileIds = relations.stream()
                .map(StudyFileRelationEntity::getFileId)
                .distinct()
                .toList();

        return studyFileMapper.selectList(
                        new LambdaQueryWrapper<StudyFileEntity>()
                                .in(StudyFileEntity::getId, fileIds)
                                .eq(StudyFileEntity::getStudyUserId, studyUserId)
                                .eq(StudyFileEntity::getStatus, 1))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 获取临时下载地址
     */
    @Override
    public StudyFileDownloadVO getDownloadUrl(
            String studyUserId,
            Long fileId) {

        StudyFileEntity entity =
                requireOwnedFile(studyUserId, fileId);

        if (entity.getStatus() != 1) {
            throw new IllegalStateException("文件当前不可下载");
        }

        int expireSeconds =
                qiniuProperties.getDownloadExpireSeconds();

        String downloadUrl =
                storageProvider.generateDownloadUrl(
                        entity.getFileKey(),
                        Duration.ofSeconds(expireSeconds));

        StudyFileDownloadVO vo =
                new StudyFileDownloadVO();

        vo.setFileId(fileId);
        vo.setDownloadUrl(downloadUrl);
        vo.setExpireSeconds(expireSeconds);

        return vo;
    }

    /**
     * 删除文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(
            String studyUserId,
            Long fileId) {

        StudyFileEntity entity =
                requireOwnedFile(studyUserId, fileId);

        // 云端删除成功后，再删除数据库记录
        storageProvider.delete(entity.getFileKey());

        studyFileMapper.deleteById(fileId);

        relationMapper.delete(
                new LambdaQueryWrapper<StudyFileRelationEntity>()
                        .eq(StudyFileRelationEntity::getFileId, fileId)
                        .eq(StudyFileRelationEntity::getStudyUserId, studyUserId));

        studyFileMapper.deleteById(fileId);
    }

    /**
     * 查询并校验文件归属
     */
    private StudyFileEntity requireOwnedFile(
            String studyUserId,
            Long fileId) {

        requireUser(studyUserId);

        if (fileId == null) {
            throw new IllegalArgumentException("文件ID不能为空");
        }

        StudyFileEntity entity =
                studyFileMapper.selectOne(
                        new LambdaQueryWrapper<StudyFileEntity>()
                                .eq(StudyFileEntity::getId, fileId)
                                .eq(StudyFileEntity::getStudyUserId, studyUserId)
                                .eq(StudyFileEntity::getStatus, 1));

        if (entity == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "文件不存在或无权访问");
        }

        return entity;
    }

    private StudyFileResponse toResponse(
            StudyFileEntity entity) {

        StudyFileResponse response =
                new StudyFileResponse();

        response.setId(entity.getId());
        response.setStudyUserId(entity.getStudyUserId());
        response.setOriginalName(entity.getOriginalName());
        response.setExtension(entity.getExtension());
        response.setContentType(entity.getContentType());
        response.setFileSize(entity.getFileSize());
        response.setStatus(entity.getStatus());
        response.setCreateTime(entity.getCreateTime());

        return response;
    }

    private String generateFileKey(
            String studyUserId,
            String extension) {

        String date = LocalDateTime.now()
                .toLocalDate()
                .toString()
                .replace("-", "/");

        return "study/" + studyUserId
                + "/" + date
                + "/" + UUID.randomUUID().toString().replace("-", "")
                + extension;
    }

    private String getExtension(String fileName) {

        int index = fileName.lastIndexOf(".");

        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(index).toLowerCase(Locale.ROOT);
    }

    private void requireUser(String studyUserId) {

        if (studyUserId == null || studyUserId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "用户未登录");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindRelation(
            String studyUserId,
            FileRelationRequest request) {

        requireUser(studyUserId);

        if (request == null
                || request.getFileId() == null
                || request.getBusinessId() == null
                || request.getBusinessType() == null
                || request.getBusinessType().isBlank()) {
            throw new IllegalArgumentException("关联参数不完整");
        }

        // 先验证文件属于当前用户
        requireOwnedFile(studyUserId, request.getFileId());

        // 防止重复关联
        Long count = relationMapper.selectCount(
                new LambdaQueryWrapper<StudyFileRelationEntity>()
                        .eq(StudyFileRelationEntity::getFileId, request.getFileId())
                        .eq(StudyFileRelationEntity::getBusinessType, request.getBusinessType())
                        .eq(StudyFileRelationEntity::getBusinessId, request.getBusinessId()));

        if (count > 0) {
            return;
        }

        StudyFileRelationEntity entity =
                new StudyFileRelationEntity();

        entity.setFileId(request.getFileId());
        entity.setStudyUserId(studyUserId);
        entity.setBusinessType(request.getBusinessType());
        entity.setBusinessId(request.getBusinessId());
        entity.setDeleted(0);
        entity.setCreateTime(LocalDateTime.now());

        relationMapper.insert(entity);
    }
}