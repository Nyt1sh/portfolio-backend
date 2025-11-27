package com.nitish.portfolio.portfolio_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

//package com.nitish.portfolio.portfolio_api.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${RESEND_API_KEY}")
//    private String resendApiKey;
//
//    // Hardcoded "from" email allowed by Resend (no domain verification required)
//    private static final String FROM_EMAIL = "onboarding@resend.dev";
//
//    @Async
//    public void sendSimpleEmail(String to, String subject, String text) {
//        try {
//            String url = "https://api.resend.com/emails";
//
//            Map<String, Object> body = new HashMap<>();
//            body.put("from", FROM_EMAIL);
//            body.put("to", List.of(to));
//            body.put("subject", subject);
//            body.put("text", text);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(resendApiKey);
//
//            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//            restTemplate.postForEntity(url, request, String.class);
//
//            System.out.println("📨 Email sent to " + to);
//
//        } catch (Exception e) {
//            System.out.println("❌ Email sending failed: " + e.getMessage());
//        }
//    }
//}
//
