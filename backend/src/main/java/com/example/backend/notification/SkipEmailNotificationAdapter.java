package com.example.backend.notification;

import org.springframework.stereotype.Component;

@Component
public class SkipEmailNotificationAdapter implements NotificationPort {
    @Override
    public void notify(String subject, String recipient, String message) {
    }
}
