package com.example.springrabbitmqproducer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${jsa.rabbitmq.queue}")
    private String queue;

    @Value("${jsa.rabbitmq.exchange}")
    private String exchange;

    @Value("${jsa.rabbitmq.routingkey}")
    private String routingkey;

    @Bean
    Queue queue() {
        return new Queue(this.queue, false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(this.exchange);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(this.routingkey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(this.jsonMessageConverter());

        return rabbitTemplate;
    }
}
