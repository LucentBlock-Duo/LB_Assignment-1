package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.model.ResponseCarManufacturerDTO;
import com.lucentblock.assignment2.repository.CarManufacturerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarManufacturerService {

    private final CarManufacturerRepository carManufacturerRepository;

    public List<ResponseCarManufacturerDTO> readAll(){
        return carManufacturerRepository.findCarManufacturersAll().stream()
                                            .map(CarManufacturer::toDto).toList();
    }
}
