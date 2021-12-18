package com.grandhotel.booking.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ControllersConfig {
    @Bean
    Integer pageSize(){
        return 10;
    }
}
