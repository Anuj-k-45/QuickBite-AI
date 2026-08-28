package com.quickbite.buildingblocks.mediator.abstractions;

@FunctionalInterface
public interface IRequestHandler<TRequest extends IRequest<TResponse>, TResponse> {
    TResponse handle(TRequest request);
}