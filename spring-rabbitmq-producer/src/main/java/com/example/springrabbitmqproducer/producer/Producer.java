package com.example.springrabbitmqproducer.producer;

import com.example.springrabbitmqproducer.model.Company;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${jsa.rabbitmq.exchange}")
    private String exchange;

    @Value("${jsa.rabbitmq.routingkey}")
    private String routingkey;

    public void sendCompany(Company company) {
        this.rabbitTemplate.convertAndSend(this.exchange, this.routingkey, company);
        System.out.println("Send msg = " + company);
    }
}
