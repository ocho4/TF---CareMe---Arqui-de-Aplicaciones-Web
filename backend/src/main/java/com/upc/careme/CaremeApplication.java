package com.upc.careme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaremeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaremeApplication.class, args);
    }

}
