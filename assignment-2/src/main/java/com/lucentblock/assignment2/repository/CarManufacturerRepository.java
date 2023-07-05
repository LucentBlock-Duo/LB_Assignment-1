package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.CarManufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarManufacturerRepository extends JpaRepository<CarManufacturer, Long> {
}
