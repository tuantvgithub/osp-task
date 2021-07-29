package com.example.springrabbitmqconsumer.consumer;

import com.example.springrabbitmqconsumer.model.Company;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @RabbitListener(queues = "${jsa.rabbitmq.queue}", containerFactory = "jsaFactory")
    public void receivedCompany(Company company) {
        System.out.println("Received Company: " + company);
    }
}
