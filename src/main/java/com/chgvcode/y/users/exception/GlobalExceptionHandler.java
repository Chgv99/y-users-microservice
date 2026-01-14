package com.chgvcode.y.users.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleException(RuntimeException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle(getTitleFromException(ex));
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle(getTitleFromException(ex));
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    private String getTitleFromException(RuntimeException exception) {
        String[] segments = exception.getClass().getName().split("\\.");

        String withSpaces = segments[segments.length - 1].replaceAll("([a-z])([A-Z])", "$1 $2");

        // return withSpaces.substring(0, 1).toUpperCase()
        // + withSpaces.substring(1).toLowerCase();

        int lastSpace = withSpaces.lastIndexOf(' ');
        if (lastSpace == -1) {
            return ""; // solo había una palabra
        }

        return withSpaces.substring(0, lastSpace);
    }
}
