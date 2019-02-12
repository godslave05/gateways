package com.ramniel.gateways.controller;

import com.ramniel.gateways.assembler.GatewayResourceAssembler;
import com.ramniel.gateways.exception.DeviceNotFoundException;
import com.ramniel.gateways.exception.GatewayNotFoundException;
import com.ramniel.gateways.exception.InvalidIpAddressException;
import com.ramniel.gateways.exception.NoMoreDeviceAllowedException;
import com.ramniel.gateways.model.Device;
import com.ramniel.gateways.model.Gateway;
import com.ramniel.gateways.repository.GatewayRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class GatewayController {

    private final GatewayRepository repository;

    private final GatewayResourceAssembler assembler;

    GatewayController(GatewayRepository repository, GatewayResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //Get all
    @GetMapping("/gateways")
    public Resources<Resource<Gateway>> all() {

        List<Resource<Gateway>> gateways = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(gateways,
                linkTo(methodOn(GatewayController.class).all()).withSelfRel());
    }

    //Create
    @PostMapping("/gateways")
    public ResponseEntity<?> newGateway(@RequestBody Gateway newGateway) throws URISyntaxException {
        if (!isValidIP(newGateway.getIp())) throw new InvalidIpAddressException(newGateway.getIp());

        Resource<Gateway> resource = assembler.toResource(repository.save(newGateway));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    //Add device
    @PostMapping("/gateways/{serial}/device")
    public ResponseEntity<?> addDevice(@RequestBody Device newDevice, @PathVariable String serial) throws URISyntaxException {
        Resource<Gateway> resource = assembler.toResource(repository.findById(serial)
                .map(gateway -> {
                    if (gateway.getDevices().size() == 10) {
                        throw new NoMoreDeviceAllowedException();
                    }

                    Device device = new Device(newDevice.getVendor(), newDevice.getStatus());
                    device.setGateway(gateway);

                    gateway.getDevices().add(device);

                    return repository.save(gateway);
                }).orElseThrow(() -> new GatewayNotFoundException(serial)));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    //Get one
    @GetMapping("/gateways/{serial}")
    public Resource<Gateway> one(@PathVariable String serial) {

        Gateway gateway = repository.findById(serial)
                .orElseThrow(() -> new GatewayNotFoundException(serial));

        return assembler.toResource(gateway);
    }

    //Delete
    @DeleteMapping("/gateways/{serial}")
    public ResponseEntity<?> deleteGateway(@PathVariable String serial) {
        repository.deleteById(serial);

        return ResponseEntity.noContent().build();
    }

    //Delete device
    @DeleteMapping("/gateways/{serial}/device/{uid}")
    public ResponseEntity<?> deleteGateway(@PathVariable String serial, @PathVariable Long uid) {
        repository.findById(serial)
                .map(gateway -> {
                    boolean removed = false;
                    for (int i = 0; i < gateway.getDevices().size(); i++) {
                        Device device = gateway.getDevices().get(i);
                        if (device.getUid().equals(uid)) {
                            gateway.getDevices().remove(i);
                            removed = true;
                            device.setGateway(null);
                            break;
                        }
                    }
                    if (!removed) throw new DeviceNotFoundException(uid);
                    return repository.save(gateway);
                }).orElseThrow(() -> new GatewayNotFoundException(serial));

        return ResponseEntity.noContent().build();
    }


    //Utils
    private boolean isValidIP(String ip) {
        Pattern pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
        Matcher matcher = pattern.matcher(ip);
        return matcher.find();
    }
}