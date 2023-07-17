package com.lucentblock.assignment2;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.repository.RepairManRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequiredArgsConstructor
public class HomeController {
    @GetMapping("/open")
    public String Home() {
        return "Hello, Home!";
    }

    @GetMapping("/secured")
    public String secured() {
        return "Hello, Secured";
    }
}
