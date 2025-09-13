package com.allan.climberanalyzer.UserHandling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendHtmlVerificationEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Email Verification Code");
            helper.setText(buildHtmlVerificationEmailBody(verificationCode), true);

            javaMailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildHtmlVerificationEmailBody(String verificationCode) {
        return String.format(
                "<html><body>" +
                        "<h2>Welcome to our platform!</h2>" +
                        "<p>Please use the following verification code to complete your registration:</p>" +
                        "<div style='background-color: #f0f0f0; padding: 20px; text-align: center; margin: 20px 0;'>" +
                        "<h1 style='color: #333; font-family: Arial, sans-serif; letter-spacing: 5px;'>%s</h1>" +
                        "</div>" +
                        "<p><strong>This code will expire in 15 minutes.</strong></p>" +
                        "<p>If you didn't create an account, please ignore this email.</p>" +
                        "<br><p>Best regards,<br>Your App Team</p>" +
                        "</body></html>",
                verificationCode);
    }
}
