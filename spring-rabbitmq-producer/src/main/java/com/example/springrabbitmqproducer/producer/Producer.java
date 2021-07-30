package com.example.springrabbitmqproducer.producer;

import com.example.springrabbitmqproducer.model.Company;
import com.example.springrabbitmqproducer.model.Product;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Producer {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${jsa.rabbitmq.exchange}")
    private String exchange;

    @Value("${jsa.rabbitmq.routingkey}")
    private String routingkey;

    public void sendCompany(Company company) {
        Scanner scanner = new Scanner(System.in);
        String s = "";

        System.out.println("------------- enter to send or press stop to stop");
        while (true) {
            this.rabbitTemplate.convertAndSend(this.exchange, this.routingkey, company);
            System.out.println("Send msg = " + company);
            s = scanner.nextLine();
            if (s.equals("stop")) break;
        }
    }

    public void sendProduct(Product product) {
        this.rabbitTemplate.convertAndSend(this.exchange, this.routingkey, product);
        System.out.println("Send msg = " + product);
    }
}
