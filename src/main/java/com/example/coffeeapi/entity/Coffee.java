package com.example.coffeeapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Coffee: representa la tabla "coffees" en PostgreSQL. Mapea TODAS las
 * columnas del dataset
 */
@Entity
@Table(name = "coffees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coffee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ---------- Informacion general del cafe ----------
	private String species; // Arabica / Robusta
	private String owner;

	@Column(nullable = false)
	private String country; // Country.of.Origin en el dataset

	private String farmName;
	private String lotNumber;
	private String mill;
	private String icoNumber;
	private String company;
	private String altitude; // viene como texto libre ("1950-2200")
	private String region;
	private String producer;
	private Integer numberOfBags;
	private String bagWeight; // p.ej. "60 kg"
	private String inCountryPartner;
	private String harvestYear; // texto libre ("2014", "2013/2014")
	private String gradingDate; // texto libre ("April 4th, 2015")
	private String variety;
	private String processingMethod; // Washed / Wet, Natural / Dry, Honey, etc.

	// ---------- Puntuaciones sensoriales (0 - 10 cada una) ----------
	private Double aroma;
	private Double flavor;
	private Double aftertaste;
	private Double acidity;
	private Double body;
	private Double balance;
	private Double uniformity;
	private Double cleanCup;
	private Double sweetness;
	private Double cupperPoints;

	/**
	 * Puntuacion total (0 - 100). Suma ponderada de las anteriores. Es lo que el
	 * DTO expone como "score".
	 */
	private Double totalCupPoints;

	// ---------- Informacion fisica del grano ----------
	private Double moisture; // humedad (0 - 1)
	private Integer categoryOneDefects;
	private Integer quakers; // granos no maduros
	private String color; // Green, Bluish-Green, etc.
	private Integer categoryTwoDefects;
	private String expiration; // fecha de caducidad (texto)

	// ---------- Certificacion ----------
	private String certificationBody;
	private String certificationAddress;
	private String certificationContact;

	// ---------- Altitud procesada ----------
	private String unitOfMeasurement; // "m" o "ft"
	private Double altitudeLowMeters;
	private Double altitudeHighMeters;
	private Double altitudeMeanMeters;
}
