package com.example.coffeeapi.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuracion de Spring Security.
 *
 * Reglas:
 *   - GET /coffees/**      -> publico
 *   - POST/PUT/DELETE /coffees/** -> requiere ROLE_ADMIN
 *   - /me                  -> requiere login (cualquier rol)
 *   - Swagger              -> publico
 *
 * Roles:
 *   El custom OAuth2UserService asigna ROLE_ADMIN si el username de GitHub
 *   esta en la lista coffee.admin-users; en otro caso, ROLE_USER.
 *
 * CORS:
 *   Permite que Angular (localhost:4200) llame a la API con credenciales.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${coffee.admin-users}")
    private String adminUsersStr;

    @Value("${coffee.frontend-url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth
                        // Swagger publico
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Lecturas de cafes -> publico
                        .requestMatchers(HttpMethod.GET, "/coffees/**").permitAll()
                        // Escrituras -> solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/coffees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/coffees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/coffees/**").hasRole("ADMIN")
                        // Resto (incluye /me) -> autenticado
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        // Usa nuestro servicio custom que asigna roles
                        .userInfoEndpoint(ui -> ui.userService(oauth2UserService()))
                        // Al loguearse con exito, redirige a Angular
                        .defaultSuccessUrl(frontendUrl, true)
                );

        return http.build();
    }

    /**
     * Servicio OAuth2 personalizado: ademas de cargar el usuario de GitHub,
     * le anade ROLE_ADMIN o ROLE_USER segun la lista de admins configurada.
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        List<String> adminUsers = Arrays.stream(adminUsersStr.split(","))
                .map(String::trim)
                .toList();

        return userRequest -> {
            OAuth2User user = delegate.loadUser(userRequest);
            String login = user.getAttribute("login");

            // Comparacion case-insensitive: da igual que el username este en
            // mayusculas o minusculas en GitHub vs application.properties.
            boolean isAdmin = login != null && adminUsers.stream()
                    .anyMatch(admin -> admin.equalsIgnoreCase(login));

            Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
            if (isAdmin) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return new DefaultOAuth2User(authorities, user.getAttributes(), "id");
        };
    }

    /**
     * Configura CORS: permite que el frontend Angular (localhost:4200)
     * llame a la API enviando cookies de sesion.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
