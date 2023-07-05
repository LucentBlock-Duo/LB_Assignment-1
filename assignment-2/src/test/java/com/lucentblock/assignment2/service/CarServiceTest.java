package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.model.CarInfoDTO;
import com.lucentblock.assignment2.model.CarInfoUpdateRequestDTO;
import com.lucentblock.assignment2.model.CreateCarRequestDTO;
import com.lucentblock.assignment2.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    CarRepository carRepository;

    @Mock
    CarInfoDTO carInfoDTO;

    @InjectMocks
    CarService carService;

    Car car;
    CarManufacturer carManufacturer;
    User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .role(Role.ROLE_USER)
                .isEmailVerified(false)
                .phoneNumber("testPhoneNumber")
                .refreshToken("refresh_token")
                .createdAt(LocalDateTime.now())
                .build();

        carManufacturer = new CarManufacturer();
        carManufacturer.setId(999L);
        carManufacturer.setName("TESTMANUFACTURER");
        carManufacturer.setCreatedAt(LocalDateTime.now());

        car = Car.builder()
                .name("testCarName")
                .carManufacturer(carManufacturer)
                .licensePlateNo("testLicensePlateNo")
                .boughtAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    @DisplayName("자신의 차량 정보를 등록할 수 있다.")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void createCar() {
        // given
        CreateCarRequestDTO testRequest = CreateCarRequestDTO.builder()
                .carName("testCarName")
                .carManufacturerId(999L)
                .licensePlateNo("testLicensePlateNo")
                .boughtAt(LocalDateTime.now())
                .userEmail("test@test.com")
                .build();

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());
        given(carRepository.save(any(Car.class))).willReturn(car);

        // when
        carService.createCar(testRequest, mock(User.class), mock(CarManufacturer.class));

        // then
        verify(carRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("자신의 차량 정보를 읽어올 수 있다.")
    void fetchCarInfo() {
        // given
        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(car));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));

        // when
        CarInfoDTO carInfoDTO = carService.fetchCarInfo(car.getLicensePlateNo());

        // then
        assertEquals(car.getLicensePlateNo(), carInfoDTO.getLicensePlateNo());
        assertEquals(car.getUser().getName(), carInfoDTO.getUserName());
        assertEquals(car.getUser().getEmail(), carInfoDTO.getUserEmail());
        assertEquals(car.getCarManufacturer().getName(), carInfoDTO.getCarManufacturerName());
        assertEquals(car.getBoughtAt(), carInfoDTO.getBoughtAt());
    }

    @Test
    @DisplayName("자신의 차량 정보를 변경할 수 있다.")
    void updateCarInfo() {
        // given
        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(car));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));

        carManufacturer.setId(998L);
        carManufacturer.setName("changedCarManufacturerName");

        car.setCarManufacturer(carManufacturer);
        car.setName("changedCarName");
        car.setBoughtAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        given(carRepository.saveAndFlush(any())).willReturn(car);

        // when
        CarInfoDTO carInfoDTO = carService.updateCarInfo(
                CarInfoUpdateRequestDTO.builder()
                        .carName("changedCarName")
                        .carManufacturerId(998L)
                        .licensePlateNo(car.getLicensePlateNo())
                        .boughtAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0))
                        .build(), carManufacturer
        );

        // then
        assertEquals(car.getLicensePlateNo(), carInfoDTO.getLicensePlateNo());
        assertEquals("changedCarName", carInfoDTO.getCarName());
        assertEquals(car.getUser().getEmail(), carInfoDTO.getUserEmail());
        assertEquals("changedCarManufacturerName", carInfoDTO.getCarManufacturerName());
        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0, 0), carInfoDTO.getBoughtAt());
    }
    
    @Test
    @DisplayName("자신의 자동차 정보를 삭제할 수 있다.")
    void deleteCarInfo() {
        // given
        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(car));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));
        
        // when
        carService.deleteCar(car.getLicensePlateNo());

        // then
        verify(carRepository, times(1)).saveAndFlush(any(Car.class));
    }
}
