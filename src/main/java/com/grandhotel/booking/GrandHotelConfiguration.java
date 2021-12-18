package com.grandhotel.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.grandhotel.booking.repository")
public class GrandHotelConfiguration {

    @Bean
    public Integer pageSize(@Value("${com.grandhotel.api.pageSize}") String value){
        Integer res =  Integer.parseInt(value);
        return res;
    }

}
