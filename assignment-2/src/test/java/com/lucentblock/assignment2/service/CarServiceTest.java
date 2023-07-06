package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.exception.CarDuplicateException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    CarRepository carRepository;

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
    void createCar() {
        // given
        CreateCarRequestDTO testRequest = CreateCarRequestDTO.builder()
                .carName("testCarName")
                .carManufacturerId(999L)
                .licensePlateNo("testLicensePlateNo")
                .boughtAt(LocalDateTime.now())
                .build();

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());
        given(carRepository.save(any(Car.class))).willReturn(car);

        // when
        carService.createCar(testRequest, mock(User.class), mock(CarManufacturer.class));

        // then
        verify(carRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("차량번호가 중복되는 차량이 존재할 경우, CarDuplicateException 이 발생한다.")
    void createCarWithDuplicateLicensePlateNo() {
        // given
        CreateCarRequestDTO testRequest = CreateCarRequestDTO.builder()
                .carName("testCarName")
                .carManufacturerId(999L)
                .licensePlateNo("testLicensePlateNo")
                .boughtAt(LocalDateTime.now())
                .build();

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(testRequest.getLicensePlateNo())).willReturn(Optional.of(car));

        // when & then
        assertThrows(CarDuplicateException.class, () -> carService.createCar(testRequest, user, carManufacturer) );
    }

    @Test
    @DisplayName("자신의 차량 정보를 읽어올 수 있다.")
    void fetchCarInfo() {
        // given
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(car));



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
    @DisplayName("자신의 차량이 아니면 정보를 읽어올 수 없다.")
    void fetchCarInfoOfSomeoneElse() {
        // given
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("DoesNotMatch@test.com", null));


        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(car));

        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        // when & then
        assertThrows(AccessDeniedException.class, ()-> carService.fetchCarInfo(car.getLicensePlateNo()));
    }

    @Test
    @DisplayName("자신의 차량 정보를 변경할 수 있다.")
    void updateCarInfo() {
        // given
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(car));

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
    @DisplayName("자신의 차량이 아니면 차량 정보를 업데이트 할 수 없다.")
    void updateCarInfoOfSomeoneElse() {
        // given
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(car));

        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("DoesNotMatch@test.com", null));


        carManufacturer.setId(998L);
        carManufacturer.setName("changedCarManufacturerName");

        car.setCarManufacturer(carManufacturer);
        car.setName("changedCarName");
        car.setBoughtAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));

        // when & then
        assertThrows(AccessDeniedException.class, () -> carService.updateCarInfo(
                CarInfoUpdateRequestDTO.builder()
                        .carName("changedCarName")
                        .carManufacturerId(998L)
                        .licensePlateNo(car.getLicensePlateNo())
                        .boughtAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0))
                        .build(), carManufacturer
        ));
    }

    @Test
    @DisplayName("자신의 자동차 정보를 삭제할 수 있다.")
    void deleteCarInfo() {
        // given
        Car mockCar = mock(Car.class);
        given(mockCar.getUser()).willReturn(user);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(mockCar));

        // when
        carService.deleteCar(car.getLicensePlateNo());

        // then
        verify(mockCar, times(1)).delete();
        verify(carRepository, times(1)).saveAndFlush(any(Car.class));
    }

    @Test
    @DisplayName("자신의 차량이 아닌 차량 정보는 삭제할 수 없다.")
    void deleteCarInfoOfSomeoneElse() {
        // given
        Car mockCar = mock(Car.class);
        given(mockCar.getUser()).willReturn(user);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("DoesNotMatch@test.com", null));

        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(car.getLicensePlateNo()))
                .willReturn(Optional.of(mockCar));

        // when & then
        assertThrows(AccessDeniedException.class, () -> carService.deleteCar(car.getLicensePlateNo()));
        verify(mockCar, times(0)).delete();
        verify(carRepository, times(0)).saveAndFlush(mockCar);
    }

    @Test
    @DisplayName("자신의 차량 목록을 조회할 수 있다.")
    void fetchCarInfoByUser() {
        // given
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));

        Car car2 = Car.builder()
                .name("testCarName2")
                .carManufacturer(carManufacturer)
                .licensePlateNo("testLicensePlateNo2")
                .boughtAt(LocalDateTime.now())
                .user(user)
                .build();

        given(carRepository.findCarsByUserAndDeletedAtIsNull(user))
                .willReturn(List.of(car, car2));

        // when
        List<CarInfoDTO> carInfoList = carService.fetchCarInfoListByUser(user);

        // then
        assertEquals(2, carInfoList.size());
        assertEquals(CarInfoDTO.carToCarInfoDTO(car), carInfoList.get(0));
        assertEquals(CarInfoDTO.carToCarInfoDTO(car2), carInfoList.get(1));
    }

    @Test
    @DisplayName("자신의 차량 목록을 조회할 수 있다.")
    void fetchCarInfoBySomeoneElse() {
        // given
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("DoesNotMatch@test.com", null));

        // when & then
        assertThrows(AccessDeniedException.class, () -> carService.fetchCarInfoListByUser(user));
    }
}
