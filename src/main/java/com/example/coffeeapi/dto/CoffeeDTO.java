package com.example.coffeeapi.dto;

import com.example.coffeeapi.entity.Coffee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeDTO {

	// ---------- Identificador ----------
	private Long id;

	// ---------- Info general ----------
	private String country;
	private String region;
	private String species; // Arabica / Robusta
	private String variety;
	private String processingMethod;
	private String harvestYear;
	private Double altitudeMeanMeters;

	// ---------- Puntuacion total ----------
	/** total_cup_points en la entidad, pero lo exponemos como "score". */
	private Double score;

	// ---------- Puntuaciones sensoriales ----------
	private Double aroma;
	private Double flavor;
	private Double aftertaste;
	private Double acidity;
	private Double body;
	private Double balance;

	// ---------- Factory method ----------
	/**
	 * mapeo del dto ya no esta en el service
	 */
	public static CoffeeDTO fromEntity(Coffee coffee) {
		return new CoffeeDTO(coffee.getId(), coffee.getCountry(), coffee.getRegion(), coffee.getSpecies(),
				coffee.getVariety(), coffee.getProcessingMethod(), coffee.getHarvestYear(),
				coffee.getAltitudeMeanMeters(),
				coffee.getTotalCupPoints(), // <- total_cup_points expuesto como "score"
				coffee.getAroma(), coffee.getFlavor(), coffee.getAftertaste(), coffee.getAcidity(), coffee.getBody(),
				coffee.getBalance());
	}
}
