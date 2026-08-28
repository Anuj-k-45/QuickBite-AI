package com.quickbite.buildingblocks.mediator.behaviors;

import com.quickbite.buildingblocks.mediator.abstractions.IPipelineBehavior;
import com.quickbite.buildingblocks.mediator.abstractions.IRequest;
import com.quickbite.buildingblocks.mediator.abstractions.RequestHandlerDelegate;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class LoggingBehavior<TRequest extends IRequest<TResponse>, TResponse>
        implements IPipelineBehavior<TRequest, TResponse> {

    private static final Logger log = LoggerFactory.getLogger(LoggingBehavior.class);
    private final Tracer tracer;

    public LoggingBehavior(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next) {
        String requestName = request.getClass().getSimpleName();
        var currentSpan = tracer.currentSpan();
        String traceId = currentSpan != null ? currentSpan.context().traceId() : "no-trace";

        log.info("[CQRS START] Handling {} [TraceID: {}]", requestName, traceId);
        long start = System.currentTimeMillis();

        try {
            TResponse response = next.handle();
            long duration = System.currentTimeMillis() - start;
            log.info("[CQRS SUCCESS] Handled {} in {} ms [TraceID: {}]", requestName, duration, traceId);
            return response;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("[CQRS FAILURE] Failed {} in {} ms: {} [TraceID: {}]", requestName, duration, ex.getMessage(),
                    traceId);
            throw ex;
        }
    }
}