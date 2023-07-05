package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByLicensePlateNoAndDeletedAtIsNull(String licensePlateNo);
}
