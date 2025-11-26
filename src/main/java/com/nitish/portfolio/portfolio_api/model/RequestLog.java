// package: com.nitish.portfolio.portfolio_api.model

package com.nitish.portfolio.portfolio_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "request_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    private String deviceType;  // e.g. "Desktop", "Mobile"
    private String browser;     // e.g. "Chrome", "Firefox"

    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
