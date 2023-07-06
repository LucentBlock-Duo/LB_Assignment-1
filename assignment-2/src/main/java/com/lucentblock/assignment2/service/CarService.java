package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.exception.CarDuplicateException;
import com.lucentblock.assignment2.model.CarInfoUpdateRequestDTO;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    public CarInfoDTO createCar(CreateCarRequestDTO createCarRequestDTO, User user, CarManufacturer carManufacturer) {

        if (carRepository.findByLicensePlateNoAndDeletedAtIsNull(createCarRequestDTO.getLicensePlateNo()).isEmpty()) {
            Car car = Car.builder()
                    .licensePlateNo(createCarRequestDTO.getLicensePlateNo())
                    .name(createCarRequestDTO.getCarName())
                    .user(user)
                    .carManufacturer(carManufacturer)
                    .createdAt(LocalDateTime.now())
                    .boughtAt(createCarRequestDTO.getBoughtAt())
                    .build();

            Car savedCar = carRepository.save(car);

            return CarInfoDTO.carToCarInfoDTO(savedCar);
        }
        throw new CarDuplicateException(createCarRequestDTO.getLicensePlateNo());
    }

    public CarInfoDTO fetchCarInfo(String licensePlateNo) {
        Car car = carRepository.findByLicensePlateNoAndDeletedAtIsNull(licensePlateNo)
                .orElseThrow(() -> new CarNotFoundException(licensePlateNo));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!car.getUser().getEmail().equals(currentUser)) {
            log.info(currentUser +" 이 " + car.getUser().getEmail() + " 의 차량 정보 조회를 시도하였습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }

        return CarInfoDTO.carToCarInfoDTO(car);
    }

    public CarInfoDTO updateCarInfo(CarInfoUpdateRequestDTO carInfo, CarManufacturer carManufacturer) {
        Car car = carRepository.findByLicensePlateNoAndDeletedAtIsNull(carInfo.getLicensePlateNo())
                .orElseThrow(() -> new CarNotFoundException(carInfo.getLicensePlateNo()));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!car.getUser().getEmail().equals(currentUser)) {
            log.info(currentUser +" 이 " + car.getUser().getEmail() + " 의 차량 정보 변경을 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }

        car.setCarManufacturer(carManufacturer);
        car.setName(carInfo.getCarName());
        car.setBoughtAt(carInfo.getBoughtAt());
        Car savedCar = carRepository.saveAndFlush(car);

        return CarInfoDTO.carToCarInfoDTO(savedCar);
    }

    public void deleteCar(String licensePlateNo) {
        Car car = carRepository.findByLicensePlateNoAndDeletedAtIsNull(licensePlateNo)
                .orElseThrow(() -> new CarNotFoundException(licensePlateNo));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!car.getUser().getEmail().equals(currentUser)) {
            log.info(currentUser +" 이 " + car.getUser().getEmail() + " 의 차량 정보 삭제를 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }

        car.delete();
        carRepository.saveAndFlush(car);
    }

    public List<CarInfoDTO> fetchCarInfoListByUser(User user) { // 그대로 반환하면 안되고 ResponseDTO 만들어야함.
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!user.getEmail().equals(currentUser)) {
            log.info(currentUser +" 이 " + user.getEmail() + " 의 차량 정보 목록 조회를 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }

        return carRepository.findCarsByUserAndDeletedAtIsNull(user).stream()
                .map( car -> CarInfoDTO.carToCarInfoDTO(car))
                .collect(Collectors.toList());
    }
}
