package com.example.tripwise_api.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
public class HelloController {

    @GetMapping("/hello") // GET requests to /hello
    public String sayHello() {
        return "Hello, World!";
    }

}
