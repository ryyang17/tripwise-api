package com.example.tripwise_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // marks this class as rest api controller
public class HelloController {

    @GetMapping("/hello") // GET requests to /hello
    public String sayHello() {
        return "Hello, World!";
    }

}

