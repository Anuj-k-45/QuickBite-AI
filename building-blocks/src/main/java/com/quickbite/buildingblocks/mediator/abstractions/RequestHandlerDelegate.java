package com.quickbite.buildingblocks.mediator.abstractions;

@FunctionalInterface
public interface RequestHandlerDelegate<TResponse> {
    TResponse handle();
}