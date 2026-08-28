package com.quickbite.buildingblocks.mediator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.buildingblocks.mediator.abstractions.IPipelineBehavior;
import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import com.quickbite.buildingblocks.mediator.abstractions.IRequest;
import com.quickbite.buildingblocks.mediator.abstractions.IRequestHandler;
import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.buildingblocks.mediator.abstractions.RequestHandlerDelegate;

@Component
public class MediatorImpl implements Mediator {

    private final ApplicationContext applicationContext;
    private final ConcurrentHashMap<Class<?>, IRequestHandler<?, ?>> handlerCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, List<IPipelineBehavior<?, ?>>> pipelineCache = new ConcurrentHashMap<>();

    public MediatorImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <TResponse> TResponse send(IRequest<TResponse> request) {
        Objects.requireNonNull(request, "request must not be null");

        IRequestHandler<IRequest<TResponse>, TResponse> handler = resolveHandler(request);
        List<IPipelineBehavior<IRequest<TResponse>, TResponse>> pipelines = resolvePipelines(request);

        RequestHandlerDelegate<TResponse> chain = () -> handler.handle(request);

        for (var pipeline : pipelines) {
            RequestHandlerDelegate<TResponse> next = chain;
            chain = () -> pipeline.handle(request, next);
        }

        return chain.handle();
    }

    @Override
    public <TResponse> TResponse send(ICommand<TResponse> command) {
        return send((IRequest<TResponse>) command);
    }

    @Override
    public <TResponse> TResponse send(IQuery<TResponse> query) {
        return send((IRequest<TResponse>) query);
    }

    @SuppressWarnings("unchecked")
    private <TResponse> IRequestHandler<IRequest<TResponse>, TResponse> resolveHandler(IRequest<TResponse> request) {
        return (IRequestHandler<IRequest<TResponse>, TResponse>) handlerCache.computeIfAbsent(request.getClass(),
                key -> {
                    Type responseType = getResponseType(request.getClass());
                    ResolvableType resolvableType = ResolvableType.forClassWithGenerics(
                            IRequestHandler.class,
                            ResolvableType.forClass(request.getClass()),
                            ResolvableType.forType(responseType));

                    String[] beanNames = applicationContext.getBeanNamesForType(resolvableType);
                    if (beanNames.length == 0) {
                        throw new IllegalStateException("No handler registered for: " + request.getClass().getName());
                    }
                    if (beanNames.length > 1) {
                        throw new IllegalStateException(
                                "Multiple handlers registered for: " + request.getClass().getName());
                    }

                    return (IRequestHandler<?, ?>) applicationContext.getBean(beanNames[0]);
                });
    }

    @SuppressWarnings("unchecked")
    private <TResponse> List<IPipelineBehavior<IRequest<TResponse>, TResponse>> resolvePipelines(
            IRequest<TResponse> request) {
        List<IPipelineBehavior<?, ?>> rawPipelines = pipelineCache.computeIfAbsent(request.getClass(), key -> {
            String[] beanNames = applicationContext.getBeanNamesForType(IPipelineBehavior.class);
            List<IPipelineBehavior<?, ?>> behaviors = Arrays.stream(beanNames)
                    .map(name -> (IPipelineBehavior<?, ?>) applicationContext.getBean(name))
                    .collect(Collectors.toList());

            Collections.reverse(behaviors);
            return behaviors;
        });

        return rawPipelines.stream()
                .map(p -> (IPipelineBehavior<IRequest<TResponse>, TResponse>) p)
                .collect(Collectors.toList());
    }

    private Type getResponseType(Class<?> requestClass) {
        for (Type genericInterface : requestClass.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                if (IRequest.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                        || ICommand.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                        || IQuery.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                    return parameterizedType.getActualTypeArguments()[0];
                }
            }
        }
        return Object.class;
    }
}