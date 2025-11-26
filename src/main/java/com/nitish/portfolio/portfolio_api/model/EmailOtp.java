// package: com.nitish.portfolio.portfolio_api.model

package com.nitish.portfolio.portfolio_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otp;

    private LocalDateTime createdAt;

    private boolean used;
}
