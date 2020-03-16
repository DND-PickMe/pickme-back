package com.pickmebackend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class ErrorsFormatter {

    public ResponseEntity<?> badRequest(String message) {
        return new ResponseEntity<>(formatAnError(message), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> badRequest(Errors errors) {
        return new ResponseEntity<>(formatErrors(errors), HttpStatus.BAD_REQUEST);
    }

    private List<String> formatAnError(String message) {
        return Arrays.asList(message);
    }

    private List<String> formatErrors(Errors errors) {
        List<String> errorList = new LinkedList<>();

        errors.getFieldErrors().forEach(e -> errorList.add(e.getDefaultMessage()));
        errors.getGlobalErrors().forEach(e -> errorList.add(e.getDefaultMessage()));
        return errorList;
    }
}
