package com.quickbite.orders.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${quickbite.rabbitmq.orders-exchange:orders.events}")
    private String exchange;

    @Value("${quickbite.rabbitmq.orders-queue:orders.order-created.projection-queue}")
    private String queue;

    @Value("${quickbite.rabbitmq.orders-routing-key:orders.order.created}")
    private String routingKey;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue ordersProjectionQueue() {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding ordersBinding(Queue ordersProjectionQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(ordersProjectionQueue).to(ordersExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}