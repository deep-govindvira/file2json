package com.example.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    
    @MessageMapping("/hello") // endpoint to send messages
    @SendTo("/topic/greetings") // where clients receive messages
    public String greeting(String message) {
        return "Hello, " + message + "!";
    }
}

