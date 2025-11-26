//// package: com.nitish.portfolio.portfolio_api.controller
//
//package com.nitish.portfolio.portfolio_api.controller;
//
//import com.nitish.portfolio.portfolio_api.dto.ContactMessageDto;
//import com.nitish.portfolio.portfolio_api.model.ContactMessage;
//import com.nitish.portfolio.portfolio_api.repository.ContactMessageRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api")
//@CrossOrigin
//@RequiredArgsConstructor
//public class ContactMessageController {
//
//    private final ContactMessageRepository contactMessageRepository;
//
//    private ContactMessageDto toDto(ContactMessage m) {
//        return ContactMessageDto.builder()
//                .id(m.getId())
//                .name(m.getName())
//                .email(m.getEmail())
//                .phone(m.getPhone())
//                .subject(m.getSubject())
//                .message(m.getMessage())
//                .readFlag(m.getReadFlag())
//                .createdAt(m.getCreatedAt())
//                .build();
//    }
//
//    // ---- PUBLIC: visitor submits message ----
//    @PostMapping("/contact")
//    public ResponseEntity<ContactMessageDto> submitMessage(@RequestBody ContactMessageDto req) {
//        ContactMessage m = ContactMessage.builder()
//                .name(req.getName())
//                .email(req.getEmail())
//                .phone(req.getPhone())
//                .subject(req.getSubject())
//                .message(req.getMessage())
//                .readFlag(false)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        ContactMessage saved = contactMessageRepository.save(m);
//        return ResponseEntity.ok(toDto(saved));
//    }
//
//    // ---- ADMIN: list all messages (unread first) ----
//    @GetMapping("/admin/messages")
//    public ResponseEntity<List<ContactMessageDto>> getMessages() {
//        List<ContactMessage> messages =
//                contactMessageRepository.findAllByOrderByReadFlagAscCreatedAtDesc();
//
//        return ResponseEntity.ok(
//                messages.stream().map(this::toDto).collect(Collectors.toList())
//        );
//    }
//
//    // ---- ADMIN: mark as read ----
//    @PutMapping("/admin/messages/{id}/read")
//    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
//        return contactMessageRepository.findById(id)
//                .map(m -> {
//                    m.setReadFlag(true);
//                    contactMessageRepository.save(m);
//                    return ResponseEntity.ok("Marked as read");
//                })
//                .orElseGet(() -> ResponseEntity.status(404).body("Message not found"));
//    }
//
//    // ---- ADMIN: delete ----
//    @DeleteMapping("/admin/messages/{id}")
//    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
//        if (!contactMessageRepository.existsById(id)) {
//            return ResponseEntity.status(404).body("Message not found");
//        }
//        contactMessageRepository.deleteById(id);
//        return ResponseEntity.ok().build();
//    }
//}



package com.nitish.portfolio.portfolio_api.controller;

import com.nitish.portfolio.portfolio_api.dto.ContactMessageDto;
import com.nitish.portfolio.portfolio_api.model.Admin;
import com.nitish.portfolio.portfolio_api.model.ContactMessage;
import com.nitish.portfolio.portfolio_api.model.EmailOtp;
import com.nitish.portfolio.portfolio_api.repository.AdminRepository;
import com.nitish.portfolio.portfolio_api.repository.ContactMessageRepository;
import com.nitish.portfolio.portfolio_api.repository.EmailOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import com.nitish.portfolio.portfolio_api.service.EmailService;



import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final AdminRepository adminRepository;
    private final JavaMailSender mailSender;
    private final EmailService emailService;

    private ContactMessageDto toDto(ContactMessage m) {
        return ContactMessageDto.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .phone(m.getPhone())
                .subject(m.getSubject())
                .message(m.getMessage())
                .readFlag(m.getReadFlag())
                .createdAt(m.getCreatedAt())
                .build();
    }

    // ---------- 1) Request OTP ----------
    // ---------- 1) Request OTP (returns JSON) ----------
//    @PostMapping("/contact/request-otp")
//    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body) {
//        String email = body.get("email");
//        if (email == null || email.isBlank()) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
//        }
//
//        String otp = String.format("%06d", new Random().nextInt(1_000_000));
//
//        EmailOtp entity = EmailOtp.builder()
//                .email(email)
//                .otp(otp)
//                .createdAt(LocalDateTime.now())
//                .used(false)
//                .build();
//        emailOtpRepository.save(entity);
//
//        // send asynchronously and return immediately
////        String text = "Your verification code is: " + otp + "\nThis code is valid for 10 minutes.";
//        String text =
//                "Hello,\n\n"
//                        + "Thank you for reaching out. Before we continue, we just need to verify that it’s really you.\n\n"
//                        + "🔐 Your verification code: " + otp + "\n"
//                        + "⏳ This code will remain valid for the next 10 minutes.\n\n"
//                        + "We’re sorry for the extra step — your security is important to us and this helps keep your request safe.\n\n"
//                        + "Warm regards,\n"
//                        + "Nitish's Portfolio";
//
//        emailService.sendSimpleEmail(email, "Your verification code", text);
//
//        // return JSON so frontend's res.json() succeeds
//        return ResponseEntity.ok(Map.of("status", "otp_sent"));
//    }


@PostMapping("/contact/request-otp")
public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    if (email == null || email.isBlank()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
    }

    // 🔐 Simple rate limit by email: 1 OTP per 60 seconds
    EmailOtp lastOtp = emailOtpRepository.findTopByEmailOrderByCreatedAtDesc(email)
            .orElse(null);
    if (lastOtp != null) {
        long secondsSinceLast = java.time.Duration.between(
                lastOtp.getCreatedAt(), LocalDateTime.now()
        ).getSeconds();
        if (secondsSinceLast < 60) {
            return ResponseEntity.status(429)
                    .body(Map.of("error", "OTP already sent recently. Please wait a bit before trying again."));
        }
    }

    String otp = String.format("%06d", new Random().nextInt(1_000_000));

    EmailOtp entity = EmailOtp.builder()
            .email(email)
            .otp(otp)
            .createdAt(LocalDateTime.now())
            .used(false)
            .build();
    emailOtpRepository.save(entity);

    String text =
            "Hello,\n\n"
                    + "To continue, please enter the verification code below:\n\n"
                    + "Code: " + otp + "\n"
                    + "Valid for: 10 minutes\n\n"
                    + "Sorry for the extra step — this helps keep your request secure.\n\n"
                    + "Kind regards,\n"
                    + "Nitish's Portfolio";

    emailService.sendSimpleEmail(email, "Your verification code", text);

    return ResponseEntity.ok(Map.of("status", "otp_sent"));
}


    // ---------- 2) Verify OTP (returns JSON) ----------
    @PostMapping("/contact/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and OTP are required"));
        }

        return emailOtpRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .map(stored -> {
                    if (stored.isUsed()) {
                        return ResponseEntity.status(400).body(Map.of("error", "OTP already used"));
                    }
                    if (!stored.getOtp().equals(otp)) {
                        return ResponseEntity.status(400).body(Map.of("error", "Invalid OTP"));
                    }
                    if (Duration.between(stored.getCreatedAt(), LocalDateTime.now()).toMinutes() > 10) {
                        return ResponseEntity.status(400).body(Map.of("error", "OTP expired"));
                    }

                    stored.setUsed(true);
                    emailOtpRepository.save(stored);
                    return ResponseEntity.ok(Map.of("status", "ok"));
                })
                .orElseGet(() -> ResponseEntity.status(400).body(Map.of("error", "OTP not found")));
    }


    // ---------- 3) Submit message (called only after OTP verified on frontend) ----------
    @PostMapping("/contact")
    public ResponseEntity<ContactMessageDto> submitMessage(@RequestBody ContactMessageDto req) {
        ContactMessage m = ContactMessage.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .subject(req.getSubject())
                .message(req.getMessage())
                .readFlag(false)
                .createdAt(LocalDateTime.now())
                .build();

        ContactMessage saved = contactMessageRepository.save(m);

        // Send admin notification if enabled
        adminRepository.findByUsername("admin").ifPresent(admin -> {
            Boolean enabled = admin.getNotificationsEnabled();
            if (Boolean.TRUE.equals(enabled)) {
                String adminEmail = admin.getNotificationEmail();
                // inside adminRepository.findByUsername("admin").ifPresent(admin -> { ... })
                if (adminEmail != null && !adminEmail.isBlank()) {
                    String messageBody = "New message from: " + saved.getName() + "\n" +
                            "Email: " + saved.getEmail() + "\n" +
                            "Phone: " + (saved.getPhone() == null ? "-" : saved.getPhone()) + "\n" +
                            "Subject: " + (saved.getSubject() == null ? "-" : saved.getSubject()) + "\n\n" +
                            "Message:\n" + saved.getMessage();

                    // send async (use EmailService)
                    emailService.sendSimpleEmail(adminEmail, "New portfolio contact message", messageBody);
                }

            }
        });

        return ResponseEntity.ok(toDto(saved));
    }

    // ---------- 4) Admin: list messages (we will group by email in frontend) ----------
    @GetMapping("/admin/messages")
    public ResponseEntity<List<ContactMessageDto>> getMessages() {
        List<ContactMessage> messages = contactMessageRepository.findAllByOrderByReadFlagAscCreatedAtDesc();
        List<ContactMessageDto> dtos = messages.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/admin/messages/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return contactMessageRepository.findById(id)
                .map(m -> {
                    m.setReadFlag(true);
                    contactMessageRepository.save(m);
                    return ResponseEntity.ok("Marked as read");
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Message not found"));
    }

    @DeleteMapping("/admin/messages/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        if (!contactMessageRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Message not found");
        }
        contactMessageRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // A grouped view: returns a list of threads grouped by email (latest message + unread count)
    @GetMapping("/admin/message-threads")
    public ResponseEntity<List<Map<String, Object>>> getMessageThreads() {
        List<ContactMessage> messages = contactMessageRepository.findAllByOrderByCreatedAtDesc();

        // Group by email (use empty string for missing emails)
        Map<String, List<ContactMessage>> byEmail = messages.stream()
                .collect(Collectors.groupingBy(m -> m.getEmail() == null ? "" : m.getEmail()));

        // build a small DTO for each thread
        List<Map<String, Object>> threads = byEmail.entrySet().stream()
                .map(entry -> {
                    String email = entry.getKey();
                    List<ContactMessage> msgs = entry.getValue();
                    long unreadCount = msgs.stream().filter(m -> !Boolean.TRUE.equals(m.getReadFlag())).count();
                    ContactMessage latest = msgs.stream()
                            .max(Comparator.comparing(ContactMessage::getCreatedAt)).orElse(null);

                    Map<String, Object> thread = new HashMap<>();
                    thread.put("email", email);
                    thread.put("name", latest != null ? latest.getName() : null);
                    thread.put("unreadCount", unreadCount);
                    thread.put("lastMessagePreview", latest != null ? (latest.getMessage().length() > 200 ? latest.getMessage().substring(0,200) + "..." : latest.getMessage()) : "");
                    thread.put("lastMessageAt", latest != null ? latest.getCreatedAt() : null);
                    thread.put("messages", msgs.stream().map(this::toDto).collect(Collectors.toList())); // full messages included
                    return thread;
                })
                .sorted((a, b) -> {
                    LocalDateTime ta = (LocalDateTime) a.get("lastMessageAt");
                    LocalDateTime tb = (LocalDateTime) b.get("lastMessageAt");
                    if (ta == null && tb == null) return 0;
                    if (ta == null) return 1;
                    if (tb == null) return -1;
                    return tb.compareTo(ta); // newest first
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(threads);
    }

}
