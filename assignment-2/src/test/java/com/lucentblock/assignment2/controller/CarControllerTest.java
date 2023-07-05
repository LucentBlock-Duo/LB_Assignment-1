package com.lucentblock.assignment2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.model.CarInfoDTO;
import com.lucentblock.assignment2.model.CarInfoUpdateRequestDTO;
import com.lucentblock.assignment2.model.CreateCarRequestDTO;
import com.lucentblock.assignment2.repository.*;
import com.lucentblock.assignment2.security.PrincipalDetailsService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtAuthenticationFilter;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.config.CustomAccessDeniedHandler;
import com.lucentblock.assignment2.security.config.CustomEntryPoint;
import com.lucentblock.assignment2.security.config.SecurityConfiguration;
import com.lucentblock.assignment2.security.oauth.OAuth2SuccessHandler;
import com.lucentblock.assignment2.security.oauth.PrincipalOAuth2UserService;
import com.lucentblock.assignment2.service.CarService;
import com.lucentblock.assignment2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfiguration.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        JwtRefreshService.class,
        CustomAccessDeniedHandler.class,
        CustomEntryPoint.class,
        PrincipalDetailsService.class,
        OAuth2SuccessHandler.class,
        PrincipalOAuth2UserService.class})
@WebMvcTest(controllers = {CarController.class})
public class CarControllerTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    CarRepository carRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    LoginChallengeRepository loginChallengeRepository;

    @MockBean
    SignupCodeChallengeRepository signupCodeChallengeRepository;

    @MockBean
    CarManufacturerRepository carManufacturerRepository;

    @MockBean
    CarService carService;

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    User user;
    CarManufacturer carManufacturer;
    Car car;

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
    void createCar() throws Exception {
        // given
        CreateCarRequestDTO testRequest = CreateCarRequestDTO.builder()
                .carName("testCarName")
                .licensePlateNo("testLPN")
                .userEmail("test@test.com")
                .carManufacturerId(1L)
                .boughtAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                .build();

        given(carService.createCar(any(CreateCarRequestDTO.class), any(User.class), any(CarManufacturer.class)))
                .willReturn(
                        CarInfoDTO.builder()
                                .userEmail("test@test.com")
                                .userName("testName")
                                .licensePlateNo("testLPN")
                                .carName("testCarName")
                                .carManufacturerName("testManufacturer")
                                .boughtAt(LocalDateTime.now())
                                .build()
        );

        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));
        given(carManufacturerRepository.findById(any())).willReturn(Optional.of(carManufacturer));
        // when & then
        this.mockMvc.perform(
                post("/api/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(testRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("user_email").hasJsonPath())
                .andExpect(jsonPath("user_name").hasJsonPath())
                .andExpect(jsonPath("license_plate_no").hasJsonPath())
                .andExpect(jsonPath("car_name").hasJsonPath())
                .andExpect(jsonPath("car_manufacturer_name").hasJsonPath())
                .andExpect(jsonPath("bought_at").hasJsonPath());
    }

    @Test
    @DisplayName("자신의 차량 정보를 읽어올 수 있다.")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void fetchCarInfo() throws Exception {
        // given
        given(carService.fetchCarInfo(anyString()))
                .willReturn(
                        CarInfoDTO.builder()
                                .userEmail("test@test.com")
                                .userName("testName")
                                .licensePlateNo("testLPN")
                                .carName("testCarName")
                                .carManufacturerName("testManufacturer")
                                .boughtAt(LocalDateTime.now())
                                .build()
                );

        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));
        given(carManufacturerRepository.findById(any())).willReturn(Optional.of(carManufacturer));

        // when & then
        this.mockMvc.perform(
                get("/api/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        Map.of("license_plate_no", "testLPN"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("user_email").hasJsonPath())
                .andExpect(jsonPath("user_name").hasJsonPath())
                .andExpect(jsonPath("license_plate_no").hasJsonPath())
                .andExpect(jsonPath("car_name").hasJsonPath())
                .andExpect(jsonPath("car_manufacturer_name").hasJsonPath())
                .andExpect(jsonPath("bought_at").hasJsonPath());
    }

    @Test
    @DisplayName("자신의 차량 정보를 업데이트 할 수 있다.")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void updateCarInfo() throws Exception {
        // given
        CarInfoUpdateRequestDTO testRequest = CarInfoUpdateRequestDTO.builder()
                .carName("changedCarName")
                .carManufacturerId(999L)
                .licensePlateNo("neverChanged")
                .boughtAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                .build();
        given(carRepository.findByLicensePlateNoAndDeletedAtIsNull(anyString())).willReturn(Optional.of(car));
        given(carManufacturerRepository.findById(any())).willReturn(Optional.of(carManufacturer));
        carManufacturer.setId(999L);
        carManufacturer.setName("changedManufacturerName");
        car.setCarManufacturer(carManufacturer);
        car.setName(testRequest.getCarName());
        given(carService.updateCarInfo(any(), any())).willReturn(CarInfoDTO.carToCarInfoDTO(car));

        // when & then
        this.mockMvc.perform(patch("/api/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(testRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("user_email").hasJsonPath())
                .andExpect(jsonPath("user_name").hasJsonPath())
                .andExpect(jsonPath("license_plate_no").hasJsonPath())
                .andExpect(jsonPath("car_name").hasJsonPath())
                .andExpect(jsonPath("car_manufacturer_name").hasJsonPath())
                .andExpect(jsonPath("bought_at").hasJsonPath());
    }

    @Test
    @DisplayName("자신의 차량 정보를 삭제할 수 있다.")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void deleteUser() throws Exception {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));
        given(carManufacturerRepository.findById(any())).willReturn(Optional.of(carManufacturer));

        // when & then
        this.mockMvc.perform(
                        delete("/api/car")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("license_plate_no", "testLPN"))))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
