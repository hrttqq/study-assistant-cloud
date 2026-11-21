package com.study.file.service.storage;

import java.io.InputStream;
import java.time.Duration;

public interface StorageProvider {
    /**
     * 上传文件
     */
    void upload(
            String fileKey,
            InputStream inputStream,
            long fileSize,
            String contentType);

    /**
     * 生成临时下载链接
     */
    String generateDownloadUrl(
            String fileKey,
            Duration duration);

    /**
     * 判断文件是否存在
     */
    boolean exists(String fileKey);

    /**
     * 删除文件
     */
    void delete(String fileKey);

    /**
     * 获取存储平台标识
     */
    String providerCode();
}
