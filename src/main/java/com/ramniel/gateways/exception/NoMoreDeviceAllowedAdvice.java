package com.ramniel.gateways.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NoMoreDeviceAllowedAdvice {

    @ResponseBody
    @ExceptionHandler(NoMoreDeviceAllowedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String NoMoreDeviceAllowedAdvice(NoMoreDeviceAllowedException ex) {
        return ex.getMessage();
    }
}