package com.nitish.portfolio.portfolio_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // in Admin.java
    private Boolean notificationsEnabled;
    private String notificationEmail;

    private Integer failedLoginAttempts = 0;
    private LocalDateTime accountLockedUntil = null;
    private String role = "ADMIN";  // <-- Add this



}
