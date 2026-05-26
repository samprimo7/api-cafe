package com.example.coffeeapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints relacionados con el usuario autenticado.
 *
 * @AuthenticationPrincipal Spring nos inyecta automaticamente el usuario que
 *                          esta logueado en la sesion actual.
 */
@RestController
@RequestMapping("/me")
@Tag(name = "Usuario", description = "Informacion del usuario autenticado")
public class UserController {

	@Operation(summary = "Devuelve los datos del usuario logueado", description = "Solo accesible si has hecho login con GitHub. "
			+ "Si llamas sin sesion, recibes 401 / redireccion al login.")
	@GetMapping
	public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal OAuth2User user) {

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		Map<String, Object> response = new HashMap<>();
		response.put("login", user.getAttribute("login"));         // username de GitHub
		response.put("name", user.getAttribute("name"));           // nombre completo
		response.put("email", user.getAttribute("email"));         // puede ser null si no es publico
		response.put("avatarUrl", user.getAttribute("avatar_url"));// foto de perfil
		response.put("githubUrl", user.getAttribute("html_url"));  // url al perfil
		response.put("id", user.getAttribute("id"));               // id numerico

		// Roles asignados por SecurityConfig (ROLE_ADMIN o ROLE_USER)
		List<String> roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		response.put("roles", roles);
		response.put("isAdmin", roles.contains("ROLE_ADMIN"));

		return ResponseEntity.ok(response);
	}
}
