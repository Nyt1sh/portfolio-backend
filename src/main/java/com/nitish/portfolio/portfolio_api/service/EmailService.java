//package com.nitish.portfolio.portfolio_api.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//    private final JavaMailSender mailSender;
//
//    @Async
//    public void sendSimpleEmail(String to, String subject, String text) {
//        try {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(to);
//            msg.setSubject(subject);
//            msg.setText(text);
//            mailSender.send(msg);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//
//}

package com.nitish.portfolio.portfolio_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendSimpleEmail(String to, String subject, String htmlBody) {
        try {
            String url = "https://api.resend.com/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            Map<String, Object> payload = Map.of(
                    "from", fromEmail,
                    "to", to,
                    "subject", subject,
                    "html", htmlBody
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(url, request, String.class);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
