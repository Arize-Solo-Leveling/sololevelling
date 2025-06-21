package com.sololevelling.gym.sololevelling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SololevellingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SololevellingApplication.class, args);
    }

}
