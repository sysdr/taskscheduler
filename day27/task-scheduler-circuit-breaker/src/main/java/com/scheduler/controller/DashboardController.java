package com.scheduler.controller;

import com.scheduler.service.TaskService;
import com.scheduler.service.ExternalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ExternalServiceImpl externalService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("tasks", taskService.getRecentTasks());
        model.addAttribute("paymentDown", externalService.isPaymentServiceDown());
        model.addAttribute("notificationFlakey", externalService.isNotificationServiceFlakey());
        return "dashboard";
    }
}
