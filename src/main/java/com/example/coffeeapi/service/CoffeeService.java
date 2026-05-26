package com.example.coffeeapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.coffeeapi.dto.CoffeeDTO;
import com.example.coffeeapi.entity.Coffee;
import com.example.coffeeapi.repository.CoffeeRepository;

/**
 * El mapeo Entity -> DTO ya no vive aqui: ahora lo hace el propio DTO con su
 * metodo estatico CoffeeDTO.fromEntity(coffee).
 */
@Service
public class CoffeeService {

	private final CoffeeRepository coffeeRepository;

	public CoffeeService(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	public CoffeeDTO findById(Long id) {
		Coffee coffee = coffeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Cafe no encontrado con id: " + id));
		return CoffeeDTO.fromEntity(coffee);
	}

	/**
	 * Actualiza un cafe existente. Lanza excepcion si el id no existe.
	 * Solo modifica los campos que vienen en el DTO; el resto se mantiene.
	 */
	public CoffeeDTO update(Long id, CoffeeDTO dto) {
		Coffee coffee = coffeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Cafe no encontrado con id: " + id));

		coffee.setCountry(dto.getCountry());
		coffee.setRegion(dto.getRegion());
		coffee.setSpecies(dto.getSpecies());
		coffee.setVariety(dto.getVariety());
		coffee.setProcessingMethod(dto.getProcessingMethod());
		coffee.setHarvestYear(dto.getHarvestYear());
		coffee.setAltitudeMeanMeters(dto.getAltitudeMeanMeters());
		coffee.setTotalCupPoints(dto.getScore());
		coffee.setAroma(dto.getAroma());
		coffee.setFlavor(dto.getFlavor());
		coffee.setAftertaste(dto.getAftertaste());
		coffee.setAcidity(dto.getAcidity());
		coffee.setBody(dto.getBody());
		coffee.setBalance(dto.getBalance());

		Coffee saved = coffeeRepository.save(coffee);
		return CoffeeDTO.fromEntity(saved);
	}

	public CoffeeDTO create(CoffeeDTO dto) {
		Coffee coffee = new Coffee();
		coffee.setCountry(dto.getCountry());
		coffee.setRegion(dto.getRegion());
		coffee.setSpecies(dto.getSpecies());
		coffee.setVariety(dto.getVariety());
		coffee.setProcessingMethod(dto.getProcessingMethod());
		coffee.setHarvestYear(dto.getHarvestYear());
		coffee.setAltitudeMeanMeters(dto.getAltitudeMeanMeters());
		coffee.setTotalCupPoints(dto.getScore()); // score del DTO -> total_cup_points en la entidad
		coffee.setAroma(dto.getAroma());
		coffee.setFlavor(dto.getFlavor());
		coffee.setAftertaste(dto.getAftertaste());
		coffee.setAcidity(dto.getAcidity());
		coffee.setBody(dto.getBody());
		coffee.setBalance(dto.getBalance());

		Coffee saved = coffeeRepository.save(coffee);
		return CoffeeDTO.fromEntity(saved);
	}

	/**
	 * Borra un cafe por id. Lanza excepcion si no existe.
	 */
	public void delete(Long id) {
		if (!coffeeRepository.existsById(id)) {
			throw new RuntimeException("Cafe no encontrado con id: " + id);
		}
		coffeeRepository.deleteById(id);
	}

	/**
	 * Busqueda unificada + paginada. - Si "country" es null o vacio -> devuelve
	 * todos los cafes (paginados). - Si "country" tiene valor -> devuelve los que
	 * contienen ese texto en su pais (LIKE %country%).
	 */
	public Page<CoffeeDTO> search(String country, Pageable pageable) {
		Page<Coffee> resultado;

		if (country == null || country.isBlank()) {
			resultado = coffeeRepository.findAll(pageable);
		} else {
			resultado = coffeeRepository.findByCountryContainingIgnoreCase(country, pageable);
		}

		// Page.map mantiene la info de paginacion y transforma cada Coffee en
		// CoffeeDTO.
		return resultado.map(CoffeeDTO::fromEntity);
	}
}
