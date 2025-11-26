package com.nitish.portfolio.portfolio_api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/health")
    public String health() {
        return "Backend is running ✔";
    }
}