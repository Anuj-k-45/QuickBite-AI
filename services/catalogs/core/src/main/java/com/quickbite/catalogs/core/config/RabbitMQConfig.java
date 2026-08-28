package com.quickbite.catalogs.core.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${quickbite.rabbitmq.catalogs-exchange:catalogs.events}")
    private String exchange;

    @Value("${quickbite.rabbitmq.catalogs-queue:catalogs.item-created.projection-queue}")
    private String queue;

    @Value("${quickbite.rabbitmq.catalogs-routing-key:catalogs.item.created}")
    private String routingKey;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public TopicExchange catalogsExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue catalogsProjectionQueue() {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding catalogsBinding(Queue catalogsProjectionQueue, TopicExchange catalogsExchange) {
        return BindingBuilder.bind(catalogsProjectionQueue).to(catalogsExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}