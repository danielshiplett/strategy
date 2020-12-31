package org.brewman.strategydemo;

import ai.applica.spring.boot.starter.temporal.annotations.EnableTemporal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableTemporal
@SpringBootApplication
public class StrategyDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StrategyDemoApplication.class, args);
    }

}
