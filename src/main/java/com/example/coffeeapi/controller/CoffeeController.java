package com.example.coffeeapi.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coffeeapi.dto.CoffeeDTO;
import com.example.coffeeapi.service.CoffeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/coffees")
@Tag(name = "Coffees", description = "Endpoints para gestionar cafes")
public class CoffeeController {

    private final CoffeeService coffeeService;

    public CoffeeController(CoffeeService coffeeService) {
        this.coffeeService = coffeeService;
    }

    @Operation(
            summary = "Obtener un cafe por su id",
            description = "Devuelve el cafe con el id indicado. Si no existe, lanza una excepcion.")
    @GetMapping("/{id}")
    public CoffeeDTO getById(
            @Parameter(description = "Id del cafe a buscar", example = "1")
            @PathVariable Long id) {
        return coffeeService.findById(id);
    }

    @Operation(
            summary = "Crear un cafe nuevo",
            description = "Recibe un CoffeeDTO en el body y lo guarda en la base de datos. Requiere login.")
    @PostMapping
    public ResponseEntity<CoffeeDTO> create(@RequestBody CoffeeDTO dto) {
        CoffeeDTO created = coffeeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Actualizar un cafe existente",
            description = "Modifica los campos del cafe con el id dado. Requiere rol ADMIN.")
    @PutMapping("/{id}")
    public CoffeeDTO update(
            @Parameter(description = "Id del cafe a actualizar", example = "1")
            @PathVariable Long id,
            @RequestBody CoffeeDTO dto) {
        return coffeeService.update(id, dto);
    }

    @Operation(
            summary = "Borrar un cafe",
            description = "Elimina el cafe con el id dado. Requiere rol ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Id del cafe a borrar", example = "1")
            @PathVariable Long id) {
        coffeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar cafes (paginado)",
            description = "Si no pasas country, lista todos. Si lo pasas, hace una busqueda parcial "
                    + "case insensitive (basta con escribir 3-4 letras). Paginado: usa ?page=0&size=10.")
    @GetMapping("/search")
    public Page<CoffeeDTO> search(
            @Parameter(description = "Texto a buscar en el pais (parcial, sin distinguir mayusculas)", example = "eth")
            @RequestParam(required = false) String country,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return coffeeService.search(country, pageable);
    }
}
