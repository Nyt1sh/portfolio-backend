// package: com.nitish.portfolio.portfolio_api.repository

package com.nitish.portfolio.portfolio_api.repository;

import com.nitish.portfolio.portfolio_api.model.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailOrderByCreatedAtDesc(String email);
}
