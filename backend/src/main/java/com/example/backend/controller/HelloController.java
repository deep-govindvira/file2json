package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class HelloController {

    private final String hello = "Hello";

    @GetMapping
    public String getHelloUsingGetMapping() {
        return hello;
    }

    @PostMapping
    public String getHelloUsingPostMapping() {
        return hello;
    }

    @PutMapping
    public String getHelloUsingPutMapping() {
        return hello;
    }

    @DeleteMapping
    public String getHelloUsingDeleteMapping() {
        return hello;
    }

    @PatchMapping
    public String getHelloUsingPatchMapping() {
        return hello;
    }
}
