// package: com.nitish.portfolio.portfolio_api.repository

package com.nitish.portfolio.portfolio_api.repository;

import com.nitish.portfolio.portfolio_api.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    // for admin panel (latest 100)
    List<RequestLog> findTop100ByOrderByCreatedAtDesc();

    // for cleanup
    long deleteByCreatedAtBefore(Instant threshold);
}
