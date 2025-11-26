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
//
//package com.nitish.portfolio.portfolio_api.service;
//
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.StandardCharsets;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    // ---- OLD: simple text email (still usable if needed) ----
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
//    // ---- INTERNAL: generic HTML email sender with plain-text fallback ----
//    @Async
//    public void sendHtmlEmail(String to, String subject, String htmlBody, String plainTextFallback) {
//        try {
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(
//                    mimeMessage,
//                    true,
//                    StandardCharsets.UTF_8.name()
//            );
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            // plain text + HTML
//            helper.setText(plainTextFallback, htmlBody);
//
//            mailSender.send(mimeMessage);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    // ---- NEW: nice glassy OTP email (this is what controller will call) ----
//    public void sendOtpEmail(String email, String otp) {
//        String subject = "Your verification code — just one quick step";
//
//        String htmlBody = buildOtpHtml(email, otp);
//
//        String plainTextFallback =
//                "Dear " + email + ",\n\n" +
//                        "We’re sorry for the extra time — we just need to confirm it’s really you before proceeding.\n\n" +
//                        "Your verification code is: " + otp + "\n" +
//                        "This code is valid for 10 minutes.\n\n" +
//                        "If you did not request this, you can safely ignore this email.\n\n" +
//                        "Kind regards,\n" +
//                        "Nitish's Portfolio Team";
//
//        sendHtmlEmail(email, subject, htmlBody, plainTextFallback);
//    }
//
//    // ---- HTML template with glassy card + polite message ----
//    private String buildOtpHtml(String nameOrEmail, String otp) {
//        String template = """
//                <!doctype html>
//                <html>
//                <head>
//                  <meta charset="utf-8"/>
//                  <meta name="viewport" content="width=device-width,initial-scale=1"/>
//                  <style>
//                    body,html{margin:0;padding:0;width:100%;}
//                    body{
//                      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial;
//                      background: linear-gradient(135deg,#eef2ff 0%, #fef9ff 100%);
//                      -webkit-font-smoothing:antialiased;
//                    }
//                    .container{
//                      display:flex;
//                      align-items:center;
//                      justify-content:center;
//                      padding:32px;
//                    }
//                    .card{
//                      width:100%;
//                      max-width:620px;
//                      border-radius:18px;
//                      padding:28px;
//                      background: linear-gradient(180deg, rgba(255,255,255,0.75), rgba(255,255,255,0.55));
//                      box-shadow: 0 8px 30px rgba(18, 38, 63, 0.08);
//                      backdrop-filter: blur(8px);
//                      -webkit-backdrop-filter: blur(8px);
//                      border: 1px solid rgba(255,255,255,0.6);
//                      overflow:hidden;
//                    }
//                    .header{
//                      display:flex;
//                      align-items:center;
//                      gap:12px;
//                      margin-bottom:18px;
//                    }
//                    .logo{
//                      width:48px;
//                      height:48px;
//                      border-radius:12px;
//                      background: linear-gradient(135deg,#6b7cff,#b58bff);
//                      display:inline-flex;
//                      align-items:center;
//                      justify-content:center;
//                      color:white;
//                      font-weight:700;
//                      font-size:20px;
//                    }
//                    h1{
//                      font-size:18px;
//                      margin:0;
//                      color:#10203a;
//                    }
//                    p{
//                      margin:0;
//                      color:#324055;
//                      line-height:1.4;
//                    }
//                    .otp-wrap{
//                      display:flex;
//                      align-items:center;
//                      justify-content:space-between;
//                      margin-top:20px;
//                      gap:16px;
//                      flex-wrap:wrap;
//                    }
//                    .otp{
//                      font-size:28px;
//                      font-weight:700;
//                      letter-spacing:3px;
//                      padding:14px 22px;
//                      border-radius:999px;
//                      background: linear-gradient(90deg, rgba(255,255,255,0.9), rgba(255,255,255,0.65));
//                      border:1px solid rgba(0,0,0,0.05);
//                      box-shadow: 0 6px 18px rgba(15,23,42,0.06);
//                    }
//                    .capsule{
//                      display:inline-block;
//                      padding:8px 14px;
//                      border-radius:999px;
//                      background: linear-gradient(90deg,#e9f0ff,#fff0f6);
//                      font-size:13px;
//                      font-weight:600;
//                      color:#16325c;
//                      border:1px solid rgba(0,0,0,0.03);
//                    }
//                    @keyframes floaty {
//                      0% { transform: translateY(0px); }
//                      50% { transform: translateY(-6px); }
//                      100% { transform: translateY(0px); }
//                    }
//                    .logo { animation: floaty 4s ease-in-out infinite; }
//
//                    .footer{
//                      margin-top:22px;
//                      font-size:13px;
//                      color:#5b6b82;
//                    }
//                    .button{
//                      display:inline-block;
//                      margin-top:16px;
//                      padding:12px 20px;
//                      border-radius:999px;
//                      text-decoration:none;
//                      font-weight:700;
//                      background: linear-gradient(90deg,#6b7cff,#b58bff);
//                      color:white;
//                      box-shadow: 0 8px 20px rgba(107,124,255,0.18);
//                    }
//                    @media (max-width:480px){
//                      .card{padding:18px;}
//                      .otp{font-size:22px;padding:10px 16px;}
//                    }
//                  </style>
//                </head>
//                <body>
//                  <div class="container">
//                    <div class="card">
//                      <div class="header">
//                        <div class="logo">N</div>
//                        <div>
//                          <h1>Authentication required — just one short step</h1>
//                          <p class="capsule">Thank you for your patience</p>
//                        </div>
//                      </div>
//
//                      <p>Dear %s,</p>
//                      <p style="margin-top:12px;">
//                        We’re truly sorry for taking a little extra time. To keep your experience secure and personal,
//                        we just need to confirm it’s really you.
//                      </p>
//                      <p style="margin-top:8px;">
//                        Please use the verification code below to continue:
//                      </p>
//
//                      <div class="otp-wrap">
//                        <div>
//                          <div style="margin-bottom:8px;color:#5b6b82;font-size:13px;">Your verification code</div>
//                          <div class="otp">%s</div>
//                        </div>
//
//                        <div style="text-align:left;font-size:13px;color:#5b6b82;max-width:220px;">
//                          <div>• This code is valid for 10 minutes</div>
//                          <div>• Please do not share it with anyone</div>
//                          <a class="button" style="pointer-events:none;">Secure verification</a>
//                        </div>
//                      </div>
//
//                      <div class="footer">
//                        <p style="margin-top:18px;">
//                          If you did not request this, you can safely ignore this email.
//                          We sincerely apologize for any inconvenience caused and truly appreciate your understanding.
//                        </p>
//                        <p style="margin-top:8px;color:#8b98ac;">
//                          Kind regards,<br/>
//                          Nitish's Portfolio Team
//                        </p>
//                      </div>
//                    </div>
//                  </div>
//                </body>
//                </html>
//                """;
//
//        return String.format(template, nameOrEmail, otp);
//    }
//}
