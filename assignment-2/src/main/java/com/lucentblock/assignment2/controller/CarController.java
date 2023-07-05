package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.exception.CarManufacturerNotFoundException;
import com.lucentblock.assignment2.model.CarInfoDTO;
import com.lucentblock.assignment2.model.CarInfoUpdateRequestDTO;
import com.lucentblock.assignment2.model.CreateCarRequestDTO;
import com.lucentblock.assignment2.repository.CarManufacturerRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final UserRepository userRepository;
    private final CarManufacturerRepository carManufacturerRepository;
    private final CarService carService;

    @PostMapping
    public ResponseEntity createCar(@Validated @RequestBody CreateCarRequestDTO createCarRequestDTO) {
        if (!createCarRequestDTO.getUserEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        } // 현재 로그인한 유저와 신청 양식에 적힌 주소가 다르면 Reject.

        User user = userRepository.findByEmailAndDeletedAtIsNull(createCarRequestDTO.getUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException(createCarRequestDTO.getUserEmail()));
        CarManufacturer carManufacturer = carManufacturerRepository.findById(createCarRequestDTO.getCarManufacturerId())
                .orElseThrow(() -> new CarManufacturerNotFoundException(createCarRequestDTO.getCarManufacturerId().toString()));

        return ResponseEntity.ok(carService.createCar(createCarRequestDTO, user, carManufacturer));
    }

    @GetMapping
    public ResponseEntity<CarInfoDTO> fetchCarInfo(@RequestBody Map<String, String> licensePlateNo) {
        return ResponseEntity.ok(carService.fetchCarInfo(licensePlateNo.get("license_plate_no")));
    }

    @PatchMapping
    public ResponseEntity updateCarInfo(@Validated @RequestBody CarInfoUpdateRequestDTO carInfoDTO) {
        CarManufacturer carManufacturer = carManufacturerRepository.findById(carInfoDTO.getCarManufacturerId())
                .orElseThrow(() -> new CarManufacturerNotFoundException(carInfoDTO.getCarManufacturerId().toString()));

        return ResponseEntity.ok(carService.updateCarInfo(carInfoDTO, carManufacturer)); // 남이 다른 사람 것 수정 못하게 하는 로직 필요.
    }

    @DeleteMapping
    public ResponseEntity deleteCarInfo(@RequestBody Map<String, String> licensePlateNo) {
        carService.deleteCar(licensePlateNo.get("license_plate_no"));
        return ResponseEntity.ok().build();
    }
}
