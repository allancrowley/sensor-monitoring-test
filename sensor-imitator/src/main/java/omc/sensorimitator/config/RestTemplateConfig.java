package omc.sensorimitator.config;

import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for creating RestTemplate beans.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Bean definition for RestTemplate.
     * This bean is used to make HTTP requests to external services.
     *
     * @return a new instance of RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}