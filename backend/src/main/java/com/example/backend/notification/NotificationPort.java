package com.example.backend.notification;

public interface NotificationPort {
    void notify(String subject, String recipient, String message);
}
