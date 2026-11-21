package com.study.file.service.storage.qiniu;


import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.study.file.service.config.QiniuProperties;
import com.study.file.service.storage.StorageProvider;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Duration;

@Component
public class QiniuKodoStorageProvider implements StorageProvider {
    private final QiniuProperties properties;

    private final Auth auth;

    private final UploadManager uploadManager;

    private final BucketManager bucketManager;

    public QiniuKodoStorageProvider(QiniuProperties properties) {

        this.properties = properties;

        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());

        Configuration configuration = new Configuration(Region.autoRegion());

        this.uploadManager = new UploadManager(configuration);

        this.bucketManager = new BucketManager(auth, configuration);
    }

    @Override
    public void upload(String fileKey, InputStream inputStream, long fileSize, String contentType) {

        try {
            String uploadToken = auth.uploadToken(properties.getBucket(), fileKey);

            Response response = uploadManager.put(inputStream, fileKey, uploadToken, null, contentType);

            response.isOK();

        } catch (QiniuException e) {
            throw new IllegalStateException(
                    "七牛云文件上传失败", e);
        }
    }

    @Override
    public String generateDownloadUrl(String fileKey, Duration duration) {

        String domain = properties.getDomain().replaceAll("/+$", "");

        String url = domain + "/" + fileKey;

        return auth.privateDownloadUrl(url, duration.getSeconds());
    }

    @Override
    public boolean exists(String fileKey) {

        try {
            bucketManager.stat(properties.getBucket(), fileKey);

            return true;

        } catch (QiniuException e) {

            if (e.code() == 612) {
                return false;
            }

            throw new IllegalStateException(
                    "查询七牛云文件失败", e);
        }
    }

    @Override
    public void delete(String fileKey) {

        try {
            bucketManager.delete(properties.getBucket(), fileKey);

        } catch (QiniuException e) {

            // 文件不存在时视为删除完成
            if (e.code() != 612) {
                throw new IllegalStateException(
                        "七牛云文件删除失败", e);
            }
        }
    }

    @Override
    public String providerCode() {
        return "QINIU_KODO";
    }
}
