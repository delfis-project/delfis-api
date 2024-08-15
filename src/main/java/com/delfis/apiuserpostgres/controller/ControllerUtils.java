package com.delfis.apiuserpostgres.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ControllerUtils {
    public static <T> Map<String, String> verifyObject(T obj, List<String> fields) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Map<String, String> errors = new HashMap<>();

        for (String field : fields) {
            Set<ConstraintViolation<T>> violations = validator.validateProperty(obj, field);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<T> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
            }
        }

        return errors;
    }
}
