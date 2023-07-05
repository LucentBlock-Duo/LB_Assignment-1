package com.lucentblock.assignment2.repository;


import com.lucentblock.assignment2.entity.CarManufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarManufacturerRepository extends JpaRepository<CarManufacturer,Long> {
    List<CarManufacturer> findAllByDeletedAtIsNull();
    default List<CarManufacturer> findCarManufacturersAll(){return findAllByDeletedAtIsNull();}
}