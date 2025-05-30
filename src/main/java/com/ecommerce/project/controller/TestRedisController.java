package com.ecommerce.project.controller;

import com.ecommerce.project.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestRedisController {
    @Autowired
    private RedisService redisService;

    @PostMapping("/set")
    public String setValue() {
        redisService.setValue("greeting", "Hello Redis!");
        return "OK";
    }

    @GetMapping("/get")
    public String getValue() {
        return redisService.getValue("greeting");
    }
}

