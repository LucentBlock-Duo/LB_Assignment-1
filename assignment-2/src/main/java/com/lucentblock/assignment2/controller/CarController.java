package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.entity.car.CarManufacturer;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.exception.CarManufacturerNotFoundException;
import com.lucentblock.assignment2.model.CarInfoDTO;
import com.lucentblock.assignment2.model.CarInfoUpdateRequestDTO;
import com.lucentblock.assignment2.model.CreateCarRequestDTO;
import com.lucentblock.assignment2.repository.car.CarManufacturerRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.service.car.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final UserRepository userRepository;
    private final CarManufacturerRepository carManufacturerRepository;
    private final CarService carService;
    public static final String paramKeyOfLicensePlateNo = "license_plate_no";

    @PostMapping
    public ResponseEntity createCar(@Validated @RequestBody CreateCarRequestDTO createCarRequestDTO) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentUser.isEmpty()) {
            log.info("인증 정보가 없습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }

        User user = userRepository.findByEmailAndDeletedAtIsNull(currentUser)
                .orElseThrow(() -> new UsernameNotFoundException(currentUser));
        CarManufacturer carManufacturer = carManufacturerRepository.findById(createCarRequestDTO.getCarManufacturerId())
                .orElseThrow(() -> new CarManufacturerNotFoundException(createCarRequestDTO.getCarManufacturerId().toString()));

        return ResponseEntity.ok(carService.createCar(createCarRequestDTO, user, carManufacturer));
    }

    @GetMapping
    public ResponseEntity<CarInfoDTO> fetchCarInfo(@RequestParam(paramKeyOfLicensePlateNo) String licensePlateNo) {
        return ResponseEntity.ok(carService.fetchCarInfo(licensePlateNo));
    }

    @PatchMapping
    public ResponseEntity updateCarInfo(@Validated @RequestBody CarInfoUpdateRequestDTO carInfoDTO) {
        CarManufacturer carManufacturer = carManufacturerRepository.findById(carInfoDTO.getCarManufacturerId())
                .orElseThrow(() -> new CarManufacturerNotFoundException(carInfoDTO.getCarManufacturerId().toString()));

        return ResponseEntity.ok(carService.updateCarInfo(carInfoDTO, carManufacturer)); // 남이 다른 사람 것 수정 못하게 하는 로직 필요.
    }

    @DeleteMapping
    public ResponseEntity deleteCarInfo(@RequestParam(paramKeyOfLicensePlateNo) String licensePlateNo) {
        carService.deleteCar(licensePlateNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity fetchCarInfoListByUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailAndDeletedAtIsNull(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

        return ResponseEntity.ok(carService.fetchCarInfoListByUser(user)); // 그대로 반환하면 안되고 ResponseDTO 만들어야함.
    }
}
