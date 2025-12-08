package com.chgvcode.y.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/demo")
public class DemoController {
    
    @GetMapping("")
    public ResponseEntity<String> sayHello() {
        return new ResponseEntity<String>("hello from secured endpoint", HttpStatus.OK);
    }
    
}
