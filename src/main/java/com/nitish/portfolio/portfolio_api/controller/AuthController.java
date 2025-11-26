package com.nitish.portfolio.portfolio_api.controller;



import com.nitish.portfolio.portfolio_api.dto.LoginRequest;
import com.nitish.portfolio.portfolio_api.model.Admin;
import com.nitish.portfolio.portfolio_api.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.nitish.portfolio.portfolio_api.dto.LoginResponse;
import com.nitish.portfolio.portfolio_api.security.JwtService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    @GetMapping("/api/test")
    public String test() {
        return "Secure route works!";
    }


//    @PostMapping("/login")
//    public Object login(@RequestBody LoginRequest request) {
//
//        Admin admin = adminRepository.findByUsername(request.getUsername())
//                .orElse(null);
//
//        if (admin == null) {
//            return "Invalid username";
//        }
//
//        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
//            return "Invalid password";
//        }
//
//        // generate token
//        String token = jwtService.generateToken(admin.getUsername());
//
//        return new LoginResponse(token, 900000); // 15 minutes
//    }
@PostMapping("/login")
public Object login(@RequestBody LoginRequest request) {

    Admin admin = adminRepository.findByUsername(request.getUsername())
            .orElse(null);

    // To avoid user enumeration, use same message for username/password issues
    String genericError = "Invalid username or password";

    if (admin == null) {
        return genericError;
    }

    // Handle null defaults safely
    Integer currentFails = admin.getFailedLoginAttempts() == null
            ? 0
            : admin.getFailedLoginAttempts();

    // 1) Check if account is currently locked
    if (admin.getAccountLockedUntil() != null &&
            admin.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
        return "Account temporarily locked due to too many failed attempts. Please try again later.";
    }

    // 2) Wrong password → increase counter and maybe lock
    if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
        currentFails = currentFails + 1;
        admin.setFailedLoginAttempts(currentFails);

        if (currentFails >= 5) { // 5 tries
            admin.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15));
        }

        adminRepository.save(admin);
        return genericError;
    }

    // 3) Successful login → reset counters
    admin.setFailedLoginAttempts(0);
    admin.setAccountLockedUntil(null);
    adminRepository.save(admin);

    // 4) generate token
    String token = jwtService.generateToken(admin.getUsername());

    return new LoginResponse(token, 900000); // 15 minutes
}


}
