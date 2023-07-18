package com.lucentblock.assignment2.repository.car;


import com.lucentblock.assignment2.entity.car.CarManufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarManufacturerRepository extends JpaRepository<CarManufacturer,Long> {
    List<CarManufacturer> findAllByDeletedAtIsNull();
    default List<CarManufacturer> findCarManufacturersAll(){return findAllByDeletedAtIsNull();}
}
