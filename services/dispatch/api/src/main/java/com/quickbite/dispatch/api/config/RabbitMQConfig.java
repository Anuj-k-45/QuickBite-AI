package com.quickbite.dispatch.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Restaurant Event Routing ---
    @Bean
    public TopicExchange restaurantExchange() {
        return new TopicExchange("restaurant.exchange");
    }

    @Bean
    public Queue restaurantCreatedDispatchQueue() {
        return new Queue("restaurant.created.dispatch.queue", true);
    }

    @Bean
    public Binding restaurantCreatedBinding(Queue restaurantCreatedDispatchQueue, TopicExchange restaurantExchange) {
        return BindingBuilder.bind(restaurantCreatedDispatchQueue)
                .to(restaurantExchange)
                .with("restaurant.created.#");
    }

    // --- Order Event Routing ---
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order.exchange");
    }

    @Bean
    public Queue orderCreatedDispatchQueue() {
        return new Queue("order.created.dispatch.queue", true);
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedDispatchQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedDispatchQueue)
                .to(orderExchange)
                .with("order.created.#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}