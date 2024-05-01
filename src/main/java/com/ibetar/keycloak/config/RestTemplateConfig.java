package com.ibetar.keycloak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**

 Configuration class for RestTemplate.
 The RestTemplateConfig class provides a bean definition for RestTemplate.
 It creates and configures an instance of RestTemplate that can be used for making REST ful API calls.
 This class is annotated with @Configuration to indicate that it is a configuration class.
 @since [current_date]

 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}