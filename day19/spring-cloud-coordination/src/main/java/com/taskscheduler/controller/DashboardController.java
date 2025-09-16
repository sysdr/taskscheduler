package com.taskscheduler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/ui/index.html";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/ui/index.html";
    }
}
