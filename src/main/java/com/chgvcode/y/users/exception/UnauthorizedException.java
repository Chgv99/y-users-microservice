package com.chgvcode.y.users.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Client is not authorized");
    }
}
