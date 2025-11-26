// package: com.nitish.portfolio.portfolio_api.logging

package com.nitish.portfolio.portfolio_api.logging;

import com.nitish.portfolio.portfolio_api.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class RequestLogCleanupJob {

    private final RequestLogRepository logRepository;

    // run once a day at 03:00 AM
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldLogs() {
        Instant threshold = Instant.now().minus(40, ChronoUnit.DAYS);
        long deleted = logRepository.deleteByCreatedAtBefore(threshold);
        System.out.println("RequestLogCleanupJob: deleted " + deleted + " old logs");
    }
}
