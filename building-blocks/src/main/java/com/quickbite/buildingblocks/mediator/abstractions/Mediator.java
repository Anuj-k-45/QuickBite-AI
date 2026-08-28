package com.quickbite.buildingblocks.mediator.abstractions;

public interface Mediator {
    <TResponse> TResponse send(IRequest<TResponse> request);

    <TResponse> TResponse send(ICommand<TResponse> command);

    <TResponse> TResponse send(IQuery<TResponse> query);
}