package com.study.file.service.storage.tencent;

import com.study.file.service.storage.StorageProvider;

import java.io.InputStream;
import java.time.Duration;

public class TencentCosStorageProvider implements StorageProvider {
    @Override
    public void upload(String fileKey, InputStream inputStream, long fileSize, String contentType) {

    }

    @Override
    public String generateDownloadUrl(String fileKey, Duration duration) {
        return "";
    }

    @Override
    public boolean exists(String fileKey) {
        return false;
    }

    @Override
    public void delete(String fileKey) {

    }

    @Override
    public String providerCode() {
        return "";
    }
}
