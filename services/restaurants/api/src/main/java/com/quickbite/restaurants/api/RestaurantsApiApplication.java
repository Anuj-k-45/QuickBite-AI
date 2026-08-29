package com.quickbite.restaurants.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.quickbite.buildingblocks",
        "com.quickbite.restaurants.core",
        "com.quickbite.restaurants.api"
})
@EntityScan(basePackages = {
        "com.quickbite.restaurants.core",
        "com.quickbite.buildingblocks.outbox"
})
@EnableJpaRepositories(basePackages = {
        "com.quickbite.restaurants.core",
        "com.quickbite.buildingblocks.outbox"
})
@EnableMongoRepositories(basePackages = "com.quickbite.restaurants.core.projections")
@EnableScheduling
public class RestaurantsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantsApiApplication.class, args);
    }
}