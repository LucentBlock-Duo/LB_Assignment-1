package com.lucentblock.assignment2.controller;


import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.model.CreateRequestReserveDTO;
import com.lucentblock.assignment2.model.ResponseCarManufacturerDTO;
import com.lucentblock.assignment2.model.ResponseReserveDTO;
import com.lucentblock.assignment2.model.UpdateRequestReserveDTO;
import com.lucentblock.assignment2.service.CarManufacturerService;
import com.lucentblock.assignment2.service.ReserveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car_manufacturer")
@RequiredArgsConstructor
@Slf4j
public class CarManufacturerController {
    private final CarManufacturerService carManufacturerService;
    @GetMapping
    private List<ResponseCarManufacturerDTO> read(){
        return carManufacturerService.readAll();
    }
}