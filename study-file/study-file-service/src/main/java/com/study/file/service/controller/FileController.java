package com.study.file.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.file.api.FileApi;
import com.study.file.api.dto.MaterialUploadRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class FileController implements FileApi {

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, Map<String, Object>> materials = new ConcurrentHashMap<>();

    @Override
    @GetMapping("/files/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", ServiceNamesConstant.FILE, "status", "UP"));
    }

    @Override
    @PostMapping("/files")
    public ApiResponse<Map<String, Object>> upload(@RequestBody MaterialUploadRequest request) {
        long id = idGenerator.getAndIncrement();
        Map<String, Object> material = new ConcurrentHashMap<>(Map.of(
                "id", id,
                "fileName", request.fileName(),
                "fileType", request.fileType() == null ? "unknown" : request.fileType(),
                "size", request.size() == null ? 0L : request.size(),
                "url", request.url() == null ? "" : request.url(),
                "knowledgeStatus", "PENDING",
                "createdAt", LocalDateTime.now()
        ));
        materials.put(id, material);
        return ApiResponse.success(material);
    }

    @Override
    @GetMapping("/files")
    public ApiResponse<List<Map<String, Object>>> listMaterials() {
        return ApiResponse.success(materials.values().stream()
                .sorted(Comparator.comparing(item -> (Long) item.get("id")))
                .toList());
    }

    @Override
    @PostMapping("/files/{materialId}/analyze")
    public ApiResponse<Map<String, Object>> analyze(@PathVariable Long materialId) {
        Map<String, Object> material = materials.get(materialId);
        if (material == null) {
            return ApiResponse.fail(404, "material not found");
        }
        Map<String, Object> analyzed = new ConcurrentHashMap<>(material);
        analyzed.put("knowledgeStatus", "READY");
        analyzed.put("summary", "资料已完成文本解析、切片和知识库索引准备。");
        analyzed.put("analyzedAt", LocalDateTime.now());
        materials.put(materialId, analyzed);
        return ApiResponse.success(analyzed);
    }
}
