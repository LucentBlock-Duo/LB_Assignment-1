package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
