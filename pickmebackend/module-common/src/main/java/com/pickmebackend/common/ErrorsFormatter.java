package com.pickmebackend.common;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class ErrorsFormatter {

    public List<String> formatAnError(String message) {
        return Arrays.asList(message);
    }

    public List<String> formatErrors(Errors errors) {
        List<String> errorList = new LinkedList<>();

        errors.getFieldErrors().forEach(e -> errorList.add(e.getDefaultMessage()));
        errors.getGlobalErrors().forEach(e -> errorList.add(e.getDefaultMessage()));
        return errorList;
    }
}
