package com.taskscheduler.messaging.dashboard;

import com.taskscheduler.messaging.kafka.KafkaProducerService;
import com.taskscheduler.messaging.rabbitmq.RabbitMQProducerService;
import com.taskscheduler.messaging.model.TaskMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class DashboardController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    @GetMapping("/")
    public String dashboard(Model model) {
        return "dashboard";
    }

    @PostMapping("/api/kafka/send")
    @ResponseBody
    public String sendKafkaMessage(@RequestParam String topic, 
                                 @RequestParam String taskType, 
                                 @RequestParam String payload) {
        try {
            TaskMessage taskMessage = new TaskMessage(taskType, payload, 
                ThreadLocalRandom.current().nextInt(1, 6));
            kafkaProducerService.sendTaskMessage(topic, taskMessage);
            return "Success: Message sent to Kafka topic: " + topic;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/api/rabbitmq/send")
    @ResponseBody
    public String sendRabbitMQMessage(@RequestParam String routingKey, 
                                    @RequestParam String taskType, 
                                    @RequestParam String payload) {
        try {
            TaskMessage taskMessage = new TaskMessage(taskType, payload, 
                ThreadLocalRandom.current().nextInt(1, 6));
            rabbitMQProducerService.sendTaskMessage(routingKey, taskMessage);
            return "Success: Message sent to RabbitMQ routing key: " + routingKey;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
