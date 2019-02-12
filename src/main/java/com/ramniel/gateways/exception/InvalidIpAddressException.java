package com.ramniel.gateways.exception;

public class InvalidIpAddressException extends RuntimeException {

    public InvalidIpAddressException(String ip) {
        super("Invalid IP address: " + ip);
    }
}