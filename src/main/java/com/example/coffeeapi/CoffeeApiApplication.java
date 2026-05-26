package com.example.coffeeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot.
 * Aqui arranca todo el contexto: escanea paquetes, configura beans, levanta Tomcat.
 */
@SpringBootApplication
public class CoffeeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeApiApplication.class, args);
    }
}
