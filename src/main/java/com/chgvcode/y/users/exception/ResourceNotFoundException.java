package com.chgvcode.y.users.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String subject) {
        super("Resource was not found: " + subject);
    }
}
