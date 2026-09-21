package com.quickbite.search.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.quickbite.search.core",
        "com.quickbite.search.api"
})
@ComponentScan(basePackages = {
        "com.quickbite.search.core",
        "com.quickbite.search.api",

        // Shared CQRS mediator
        "com.quickbite.buildingblocks.mediator",

        // Shared authentication/security
        "com.quickbite.buildingblocks.security"
})
@EnableElasticsearchRepositories(basePackages = "com.quickbite.search.core.repositories")
public class SearchApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApiApplication.class, args);
    }
}