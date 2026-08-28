package com.quickbite.orders.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.quickbite.buildingblocks",
        "com.quickbite.orders.core",
        "com.quickbite.orders.api"
})
@EntityScan(basePackages = {
        "com.quickbite.orders.core.orders.model",
        "com.quickbite.buildingblocks.outbox"
})
@EnableJpaRepositories(basePackages = {
        "com.quickbite.orders.core.orders.data",
        "com.quickbite.buildingblocks.outbox"
})
@EnableMongoRepositories(basePackages = "com.quickbite.orders.core.orders.projections")
@EnableScheduling
public class OrdersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersApiApplication.class, args);
    }
}