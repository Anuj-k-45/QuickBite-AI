package com.quickbite.users.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.quickbite.buildingblocks",
        "com.quickbite.users.core",
        "com.quickbite.users.api"
})
@EntityScan(basePackages = {
        "com.quickbite.users.core",
        "com.quickbite.buildingblocks"
})
@EnableJpaRepositories(basePackages = {
        "com.quickbite.users.core",
        "com.quickbite.buildingblocks"
})
public class UsersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApiApplication.class, args);
    }
}