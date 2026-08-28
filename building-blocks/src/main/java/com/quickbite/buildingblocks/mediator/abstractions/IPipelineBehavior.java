package com.quickbite.buildingblocks.mediator.abstractions;

public interface IPipelineBehavior<TRequest extends IRequest<TResponse>, TResponse> {
    TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next);
}