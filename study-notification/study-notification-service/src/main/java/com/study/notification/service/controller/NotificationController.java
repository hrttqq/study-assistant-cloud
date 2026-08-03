package com.study.notification.service.controller;

import com.study.common.core.constant.ServiceNamesConstant;
import com.study.notification.api.NotificationApi;
import com.study.notification.api.dto.ReminderCreateRequest;
import org.springframework.web.bind.annotation.GetMapping;
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
public class NotificationController implements NotificationApi {

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, Map<String, Object>> reminders = new ConcurrentHashMap<>();

    @Override
    @GetMapping("/notifications/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", ServiceNamesConstant.NOTIFICATION, "status", "UP"));
    }

    @Override
    @PostMapping("/notifications")
    public ApiResponse<Map<String, Object>> createReminder(@RequestBody ReminderCreateRequest request) {
        long id = idGenerator.getAndIncrement();
        Map<String, Object> reminder = Map.of(
                "id", id,
                "title", request.title(),
                "content", request.content() == null ? "" : request.content(),
                "remindAt", request.remindAt() == null ? LocalDateTime.now().plusHours(1) : request.remindAt(),
                "status", "WAITING"
        );
        reminders.put(id, reminder);
        return ApiResponse.success(reminder);
    }

    @Override
    @GetMapping("/notifications")
    public ApiResponse<List<Map<String, Object>>> listReminders() {
        return ApiResponse.success(reminders.values().stream()
                .sorted(Comparator.comparing(item -> (Long) item.get("id")))
                .toList());
    }
}
