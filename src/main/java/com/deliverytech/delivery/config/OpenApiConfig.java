package com.deliverytech.delivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Tech API")
                        .version("1.0.0")
                        .description("Sistema completo de delivery desenvolvido com Spring Boot 3 e Java 21.")
                        .contact(new Contact()
                                .name("Guilherme Perlasca")
                                .email("perlasca47@gmail.com")
                                .url("https://github.com/guiperlasca"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://choosealicense.com/licenses/mit/")));
    }
}