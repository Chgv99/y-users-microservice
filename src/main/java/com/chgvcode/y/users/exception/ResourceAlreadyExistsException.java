package com.chgvcode.y.users.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String subject) {
        super("Resource already exists: " + subject);
    }
}
