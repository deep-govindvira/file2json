package com.example.backend.notification;

import com.example.backend.config.AppProps;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Google To step verification -> App password -> Create password
 * To disable email notifications via Spring, remove @Component, @Primary.
 */
//@Component
//@Primary
@RequiredArgsConstructor
public class EmailNotificationAdapter implements NotificationPort {
    private final JavaMailSender mailSender;
    private final AppProps appProps;

    @Override
    public void notify(String subject, String recipient, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setFrom(appProps.getSpring().getMail().getUsername());

            // enables HTML
            helper.setText(message, true);

            mailSender.send(mimeMessage);

            System.out.printf("[EMAIL HTML] to=%s%n", recipient);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}