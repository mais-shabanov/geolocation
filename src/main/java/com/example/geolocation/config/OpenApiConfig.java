package com.example.geolocation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "GeoLocation Api", version = "1.0",
        contact = @Contact(name = "Mais", email = "mais.shabanov.03@gmail.com"))
)
public class OpenApiConfig {

}