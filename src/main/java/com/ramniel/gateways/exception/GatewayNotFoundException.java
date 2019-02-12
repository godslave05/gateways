package com.ramniel.gateways.exception;

public class GatewayNotFoundException extends RuntimeException {

    public GatewayNotFoundException(String serial) {
        super("Could not find gateway " + serial);
    }
}