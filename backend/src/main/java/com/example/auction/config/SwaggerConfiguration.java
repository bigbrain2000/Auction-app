package com.example.auction.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI filterServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auction Hunters")
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Auction Hunters Documentation")
                        .url("TBD"));

    }
}
