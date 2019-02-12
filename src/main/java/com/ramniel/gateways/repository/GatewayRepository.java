package com.ramniel.gateways.repository;


import com.ramniel.gateways.model.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatewayRepository extends JpaRepository<Gateway, String> {

}