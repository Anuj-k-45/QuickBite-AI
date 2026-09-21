package com.quickbite.search.core.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RESTAURANT_EXCHANGE = "restaurant.exchange";
    public static final String RESTAURANT_SEARCH_QUEUE = "search.restaurant.created.queue";
    public static final String RESTAURANT_CREATED_ROUTING_KEY = "restaurant.created";

    public static final String CATALOGS_EXCHANGE = "catalogs.events";
    public static final String CATALOG_SEARCH_QUEUE = "search.catalog.item-created.queue";
    public static final String CATALOG_ITEM_CREATED_ROUTING_KEY = "catalogs.item.created";

    @Bean
    public TopicExchange restaurantExchange() {
        return new TopicExchange(
                RESTAURANT_EXCHANGE,
                true,
                false);
    }

    @Bean
    public Queue restaurantSearchQueue() {
        return QueueBuilder
                .durable(RESTAURANT_SEARCH_QUEUE)
                .build();
    }

    @Bean
    public Binding restaurantSearchBinding(
            Queue restaurantSearchQueue,
            TopicExchange restaurantExchange) {

        return BindingBuilder
                .bind(restaurantSearchQueue)
                .to(restaurantExchange)
                .with(RESTAURANT_CREATED_ROUTING_KEY);
    }

    @Bean
    public TopicExchange catalogsExchange() {
        return new TopicExchange(
                CATALOGS_EXCHANGE,
                true,
                false);
    }

    @Bean
    public Queue catalogSearchQueue() {
        return QueueBuilder
                .durable(CATALOG_SEARCH_QUEUE)
                .build();
    }

    @Bean
    public Binding catalogSearchBinding(
            Queue catalogSearchQueue,
            TopicExchange catalogsExchange) {

        return BindingBuilder
                .bind(catalogSearchQueue)
                .to(catalogsExchange)
                .with(CATALOG_ITEM_CREATED_ROUTING_KEY);
    }

    /*
     * Converts RabbitMQ JSON messages into the
     * corresponding shared event classes.
     *
     * Example:
     *
     * JSON + __TypeId__
     * ↓
     * RestaurantCreatedV1
     *
     * JSON + __TypeId__
     * ↓
     * ProductCreatedV1
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /*
     * Explicitly configure the RabbitMQ listener container
     * to use the JSON message converter above.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);

        return factory;
    }
}