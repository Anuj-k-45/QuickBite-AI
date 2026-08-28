package com.quickbite.buildingblocks.mediator.behaviors;

import com.quickbite.buildingblocks.exceptions.ValidationException;
import com.quickbite.buildingblocks.mediator.abstractions.IPipelineBehavior;
import com.quickbite.buildingblocks.mediator.abstractions.IRequest;
import com.quickbite.buildingblocks.mediator.abstractions.RequestHandlerDelegate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ValidationBehavior<TRequest extends IRequest<TResponse>, TResponse>
        implements IPipelineBehavior<TRequest, TResponse> {

    private final Validator validator;

    public ValidationBehavior(Validator validator) {
        this.validator = validator;
    }

    @Override
    public TResponse handle(TRequest request, RequestHandlerDelegate<TResponse> next) {
        Set<ConstraintViolation<TRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            Map<String, List<String>> errors = violations.stream()
                    .collect(Collectors.groupingBy(
                            v -> v.getPropertyPath().toString(),
                            Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())));

            throw new ValidationException(errors);
        }

        return next.handle();
    }
}