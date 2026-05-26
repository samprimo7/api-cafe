package com.example.coffeeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuracion de Swagger / OpenAPI.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI coffeeOpenAPI() {
		return new OpenAPI().info(new Info().title("Coffee API")
				.description("API REST para gestionar cafes basados en el dataset Coffee Quality Institute. "
						+ "Permite listar, buscar (con LIKE + paginacion), consultar por id y crear nuevos cafes.")
				.version("1.0.0").contact(new Contact().name("Alex").email("samprimo7@gmail.com"))
				.license(new License().name("Uso academico")));
	}
}
