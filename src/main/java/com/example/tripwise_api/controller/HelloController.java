package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController  // marks this class as rest api controller
public class HelloController {

    @GetMapping("/hello") // GET requests to /hello
    public String sayHello() {
        return "Hello, World!";
    }

}

