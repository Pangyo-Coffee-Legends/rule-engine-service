package com.nhnacademy.ruleengineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class RuleEngineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleEngineServiceApplication.class, args);
    }

}
