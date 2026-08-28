package com.quickbite.buildingblocks.exceptions;

import java.util.List;
import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, List<String>> errors;

    public ValidationException(Map<String, List<String>> errors) {
        super("Validation failed for one or more fields.");
        this.errors = errors;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}