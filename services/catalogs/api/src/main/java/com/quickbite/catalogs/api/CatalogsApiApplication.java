package com.quickbite.catalogs.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
                "com.quickbite.catalogs",
                "com.quickbite.buildingblocks",
                "com.quickbite.shared"
})
@EntityScan(basePackages = {
                "com.quickbite.catalogs.core.products.model",
                "com.quickbite.buildingblocks.outbox"
})
@EnableJpaRepositories(basePackages = {
                "com.quickbite.catalogs.core.products.data",
                "com.quickbite.buildingblocks.outbox"
})
@EnableMongoRepositories(basePackages = {
                "com.quickbite.catalogs.core.projections"
})
public class CatalogsApiApplication {
        public static void main(String[] args) {
                SpringApplication.run(CatalogsApiApplication.class, args);
        }
}