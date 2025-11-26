// package: com.nitish.portfolio.portfolio_api.controller

package com.nitish.portfolio.portfolio_api.controller;

import com.nitish.portfolio.portfolio_api.dto.AdminSettingsDto;
import com.nitish.portfolio.portfolio_api.model.Admin;
import com.nitish.portfolio.portfolio_api.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final AdminRepository adminRepository;

    // Read current admin settings (only for authenticated admin)
    @GetMapping
    public ResponseEntity<?> getSettings() {
        Optional<Admin> opt = adminRepository.findByUsername("admin");
        if (opt.isEmpty()) return ResponseEntity.status(404).body("Admin not found");

        Admin admin = opt.get();
        AdminSettingsDto dto = AdminSettingsDto.builder()
                .notificationsEnabled(admin.getNotificationsEnabled())
                .notificationEmail(admin.getNotificationEmail())
                .build();

        return ResponseEntity.ok(dto);
    }

    // Update settings (notificationsEnabled, notificationEmail)
    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody AdminSettingsDto dto) {
        Optional<Admin> opt = adminRepository.findByUsername("admin");
        if (opt.isEmpty()) return ResponseEntity.status(404).body("Admin not found");

        Admin admin = opt.get();
        admin.setNotificationsEnabled(dto.getNotificationsEnabled());
        admin.setNotificationEmail(dto.getNotificationEmail());
        adminRepository.save(admin);

        return ResponseEntity.ok(AdminSettingsDto.builder()
                .notificationsEnabled(admin.getNotificationsEnabled())
                .notificationEmail(admin.getNotificationEmail())
                .build());
    }
}
