package com.example.coffeeapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.coffeeapi.entity.Coffee;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

	Page<Coffee> findByCountryContainingIgnoreCase(String country, Pageable pageable);
}
