// package: com.nitish.portfolio.portfolio_api.controller

package com.nitish.portfolio.portfolio_api.controller;

import com.nitish.portfolio.portfolio_api.model.RequestLog;
import com.nitish.portfolio.portfolio_api.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@CrossOrigin
@RequiredArgsConstructor
public class RequestLogController {

    private final RequestLogRepository logRepository;

    @GetMapping
    public ResponseEntity<List<RequestLog>> getLatestLogs() {
        List<RequestLog> logs = logRepository.findTop100ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(logs);
    }
}
