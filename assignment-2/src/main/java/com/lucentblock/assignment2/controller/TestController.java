package com.lucentblock.assignment2.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${app.env1}")
    String env1;

    @Value("${app.env2}")
    String env2;

    @RequestMapping("/test")
    public String env(){
        return "env1: "+env1+", env2: "+env2;
    }
}
