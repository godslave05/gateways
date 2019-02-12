package com.ramniel.gateways.assembler;

import com.ramniel.gateways.controller.GatewayController;
import com.ramniel.gateways.model.Gateway;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class GatewayResourceAssembler implements ResourceAssembler<Gateway, Resource<Gateway>> {

    @Override
    public Resource<Gateway> toResource(Gateway gateway) {

        return new Resource<>(gateway,
                linkTo(methodOn(GatewayController.class).one(gateway.getSerial())).withSelfRel(),
                linkTo(methodOn(GatewayController.class).all()).withRel("gateways"));
    }
}
