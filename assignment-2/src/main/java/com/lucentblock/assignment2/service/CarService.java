package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.model.CreateCarRequestDTO;
import com.lucentblock.assignment2.model.CarInfoDTO;
import com.lucentblock.assignment2.repository.CarRepository;
import com.lucentblock.assignment2.exception.CarNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    public CarInfoDTO createCar(CreateCarRequestDTO createCarRequestDTO, User user, CarManufacturer carManufacturer) {
        Car car = Car.builder()
                .name(createCarRequestDTO.getCarName())
                .user(user)
                .carManufacturer(carManufacturer)
                .createdAt(LocalDateTime.now())
                .boughtAt(createCarRequestDTO.getBoughtAt())
                .build();

        Car savedCar = carRepository.save(car);

        return CarInfoDTO.carToCarInfoDTO(savedCar);
    }

    public CarInfoDTO fetchCarInfo(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new CarNotFoundException(String.valueOf(carId)));

        String userIssuedRequest = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!car.getUser().getEmail().equals(userIssuedRequest)) {
            log.info(userIssuedRequest +" 이 " + car.getUser().getEmail() + " 에 접근을 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }
        return CarInfoDTO.carToCarInfoDTO(car);
    }

    public CarInfoDTO updateCarInfo(CarInfoDTO carInfo) { // 수정할 수 있는 속성은 bought_at 뿐.
        Car car = carRepository.findById(carInfo.getCarId()).orElseThrow(() -> new CarNotFoundException(String.valueOf(carInfo.getCarId())));
        String userIssuedRequest = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!car.getUser().getEmail().equals(userIssuedRequest)) {
            log.info(userIssuedRequest +" 이 " + car.getUser().getEmail() + " 에 접근을 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }

        car.setBoughtAt(carInfo.getBoughtAt());
        Car savedCar = carRepository.saveAndFlush(car);

        return CarInfoDTO.carToCarInfoDTO(savedCar);
    }

    public CarInfoDTO deleteCar(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new CarNotFoundException(String.valueOf(carId)));

        String userIssuedRequest = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!car.getUser().getEmail().equals(userIssuedRequest)) {
            log.info(userIssuedRequest +" 이 " + car.getUser().getEmail() + " 에 접근을 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }

        car.delete();
        Car deletedCar = carRepository.saveAndFlush(car);

        return CarInfoDTO.carToCarInfoDTO(deletedCar);
    }
}
