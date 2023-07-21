package com.lucentblock.assignment2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.PrincipalDetailsService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtAuthenticationFilter;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.config.CustomAccessDeniedHandler;
import com.lucentblock.assignment2.security.config.CustomEntryPoint;
import com.lucentblock.assignment2.security.config.SecurityConfiguration;
import com.lucentblock.assignment2.security.oauth.OAuth2SuccessHandler;
import com.lucentblock.assignment2.security.oauth.PrincipalOAuth2UserService;
import com.lucentblock.assignment2.service.repair_shop.RepairShopService;
import com.lucentblock.assignment2.service.ReserveService;
import com.lucentblock.assignment2.service.UserService;
import com.lucentblock.assignment2.service.car.CarService;
import com.lucentblock.assignment2.service.item.ItemDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
@WebMvcTest(controllers = {ReserveController.class})
public class ReserveControllerTest {

    @MockBean
    LoginChallengeRepository loginChallengeRepository;

    @MockBean
    SignupCodeChallengeRepository signupCodeChallengeRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    UserService userService;

    @MockBean
    RepairShopService repairShopService;

    @MockBean
    CarService carService;

    @MockBean
    ItemDetailService itemDetailService;

    @MockBean
    ReserveService reserveService;

    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    User user;
    Car car;
    ItemDetail item;
    RepairShop repairShop;
    Reserve reserve;
    MaintenanceItem maintenanceItem;
    RepairMan repairMan;

    @BeforeEach
    void setup() {
        user = mock(User.class);

        User carOwner = mock(User.class);
        given(carOwner.getEmail()).willReturn("test@test.com");

        car = mock(Car.class);
        given(car.getName()).willReturn("testCar");
        given(car.getUser()).willReturn(carOwner);

        maintenanceItem = mock(MaintenanceItem.class);
        given(maintenanceItem.getItemName()).willReturn("item");

        item = mock(ItemDetail.class);
        given(item.getMaintenanceItem()).willReturn(maintenanceItem);

        repairShop = mock(RepairShop.class);
        given(repairShop.getName()).willReturn("testRepairShop");
        repairMan = RepairMan.builder()
                .id(1L)
                .name("testRepairMan")
                .careerStartAt(LocalDateTime.now())
                .licenseId(3)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        reserve = Reserve.builder()
                .id(1L)
                .startTime(LocalTime.of(9,0))
                .endTime(LocalTime.of(9,30))
                .date(LocalDate.now())
                .car(car)
                .repairMan(repairMan)
                .repairShop(repairShop)
                .itemDetail(item)
                .createdAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }
    @Test
    @DisplayName("예약을 생성할 수 있다.")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void createReserve() throws Exception{
        // given
        given(user.getEmail()).willReturn(SecurityContextHolder.getContext().getAuthentication().getName());
        given(userService.getUserByUsername(anyString())).willReturn(user);
        given(repairShopService.getRepairShopById(any())).willReturn(repairShop);
        given(carService.getCarById(any())).willReturn(car);
        given(itemDetailService.getItemById(any())).willReturn(item);

        given(user.getBalance()).willReturn(10000L);
        given(item.getPrice()).willReturn(5000);

        given(reserveService.createReserve(any(), any(), any(), any(), any()))
                .willReturn(reserve);

        // when & then
        mockMvc.perform(post("/api/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(reserve.toDto()))))
                .andDo(print())
                .andExpect(jsonPath("$[0].car_name").exists())
                .andExpect(jsonPath("$[0].repair_shop_name").exists())
                .andExpect(jsonPath("$[0].repair_man_id").exists())
                .andExpect(jsonPath("$[0].maintenance_item_name").exists())
                .andExpect(jsonPath("$[0].start_time").exists())
                .andExpect(jsonPath("$[0].end_time").exists())
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    @DisplayName("차 소유자가 아닌 유저가 예약 생성 요청시 Access Denied Exception")
    @WithMockUser(username = "doesNotMatch@test.com", authorities = "ROLE_USER")
    void createReserveWithAccessDenied() throws Exception{
        given(user.getEmail()).willReturn(SecurityContextHolder.getContext().getAuthentication().getName());
        given(userService.getUserByUsername(anyString())).willReturn(user);
        given(repairShopService.getRepairShopById(any())).willReturn(repairShop);
        given(carService.getCarById(any())).willReturn(car);
        given(itemDetailService.getItemById(any())).willReturn(item);

        given(user.getBalance()).willReturn(10000L);
        given(item.getPrice()).willReturn(5000);

        given(reserveService.createReserve(any(), any(), any(), any(), any()))
                .willReturn(reserve);

        // when & then
        mockMvc.perform(post("/api/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(reserve.toDto()))))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("잔액이 부족할 경우, Balance Not Enough Exception 발생")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void createReserveWithBalanceNotEnough() throws Exception{
        given(user.getEmail()).willReturn(SecurityContextHolder.getContext().getAuthentication().getName());
        given(userService.getUserByUsername(anyString())).willReturn(user);
        given(repairShopService.getRepairShopById(any())).willReturn(repairShop);
        given(carService.getCarById(any())).willReturn(car);
        given(itemDetailService.getItemById(any())).willReturn(item);

        given(user.getBalance()).willReturn(5000L);
        given(item.getPrice()).willReturn(10000);

        given(reserveService.createReserve(any(), any(), any(), any(), any()))
                .willReturn(reserve);

        // when & then
        mockMvc.perform(post("/api/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(reserve.toDto()))))
                .andDo(print())
                .andExpect(jsonPath("message").exists())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인한 유저의 Reserve List 를 조회할 수 있다.")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void fetchReservesByUser() throws Exception{
        // given
        given(user.getEmail()).willReturn(SecurityContextHolder.getContext().getAuthentication().getName());
        given(userService.getUserByUsername(anyString())).willReturn(user);
        given(reserveService.getReservesByUser(user)).willReturn(List.of(reserve));

        // when & then
        mockMvc.perform(get("/api/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(reserve.toDto()))))
                .andDo(print())
                .andExpect(jsonPath("$[0].car_name").exists())
                .andExpect(jsonPath("$[0].repair_shop_name").exists())
                .andExpect(jsonPath("$[0].repair_man_id").exists())
                .andExpect(jsonPath("$[0].maintenance_item_name").exists())
                .andExpect(jsonPath("$[0].start_time").exists())
                .andExpect(jsonPath("$[0].end_time").exists())
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    @DisplayName("Reserve 를 삭제할 수 있다.")
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    void deleteReserves() throws Exception{
        // Given
        given(reserveService.deleteReserves(anyList(), anyString())).willReturn(List.of(reserve));

        // When & Then
        mockMvc.perform(delete("/api/reserve")
                        .param("ids", "1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].car_name").exists())
                .andExpect(jsonPath("$[0].repair_shop_name").exists())
                .andExpect(jsonPath("$[0].repair_man_id").exists())
                .andExpect(jsonPath("$[0].maintenance_item_name").exists())
                .andExpect(jsonPath("$[0].start_time").exists())
                .andExpect(jsonPath("$[0].end_time").exists())
                .andExpect(jsonPath("$[0].date").exists());
    }
}
