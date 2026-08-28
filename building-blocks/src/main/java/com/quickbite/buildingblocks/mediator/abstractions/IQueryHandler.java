package com.quickbite.buildingblocks.mediator.abstractions;

public interface IQueryHandler<TQuery extends IQuery<TResponse>, TResponse>
                extends IRequestHandler<TQuery, TResponse> {

        @Override
        TResponse handle(TQuery query);
}