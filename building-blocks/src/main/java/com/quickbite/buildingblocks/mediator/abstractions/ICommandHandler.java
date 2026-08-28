package com.quickbite.buildingblocks.mediator.abstractions;

@FunctionalInterface
public interface ICommandHandler<TCommand extends ICommand<TResponse>, TResponse>
        extends IRequestHandler<TCommand, TResponse> {
}