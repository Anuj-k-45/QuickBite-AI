package com.quickbite.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback/catalogs")
    public Mono<ResponseEntity<String>> catalogFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"error\": \"Catalog Service is currently unavailable. Please try again later! (Fallback Triggered)\"}"));
    }

    @GetMapping("/fallback/orders")
    public Mono<ResponseEntity<String>> orderFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"error\": \"Order Service is currently unavailable. Please try again later! (Fallback Triggered)\"}"));
    }
    
    @GetMapping("/fallback/restaurants")
    public ResponseEntity<String> restaurantFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Restaurant Service is currently unavailable. Please try again later!");
    }
}