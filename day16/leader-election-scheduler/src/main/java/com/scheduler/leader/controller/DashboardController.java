package com.scheduler.leader.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/index";
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
