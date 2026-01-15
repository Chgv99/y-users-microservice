package com.chgvcode.y.users.exception;

public record FieldViolation(
    String field,
    String message,
    Object rejectedValue
) {}