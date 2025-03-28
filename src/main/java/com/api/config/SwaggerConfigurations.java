package com.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration class for setting up Swagger/OpenAPI documentation.
 */
@Configuration
public class SwaggerConfigurations {

    /**
     * Configures the OpenAPI documentation settings.
     *
     * @return the OpenAPI documentation settings
     */
    @Bean
    public OpenAPI documentationSettings() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .info(new Info().title("API Documentation")
                        .description("API for managing car and users")
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("API for managing car and users")
                                .url("https://google.com")
                )
                .tags(
                        Arrays.asList(
                                new Tag().name("Car").description("Operations related to cars"),
                                new Tag().name("User").description("Operations related to users")
                        )
                );
    }

}