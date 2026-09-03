package com.quickbite.dispatch.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.quickbite.buildingblocks.mediator", // Keep this so the Mediator bean gets registered!
        "com.quickbite.dispatch.core",
        "com.quickbite.dispatch.api"
}, exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableMongoRepositories(basePackages = "com.quickbite.dispatch.core.data")
public class DispatchApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DispatchApiApplication.class, args);
    }
}