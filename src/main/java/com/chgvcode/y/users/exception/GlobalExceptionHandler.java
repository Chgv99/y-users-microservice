package com.chgvcode.y.users.exception;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(getTitleFromException(ex));
        problem.setInstance(URI.create(request.getRequestURI()));

        List<FieldViolation> fieldViolations = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldViolation(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()))
            .toList();
        problem.setProperty("violations", fieldViolations);

        return problem;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle(getTitleFromException(ex));
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    private String getTitleFromException(Exception exception) {
        String[] segments = exception.getClass().getName().split("\\.");

        String withSpaces = segments[segments.length - 1].replaceAll("([a-z])([A-Z])", "$1 $2");

        int lastSpace = withSpaces.lastIndexOf(' ');
        if (lastSpace == -1) {
            return "";
        }

        return withSpaces.substring(0, lastSpace);
    }
}
