package com.lucentblock.assignment2;

import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.exception.*;
import com.lucentblock.assignment2.controller.ReserveController;
import com.lucentblock.assignment2.service.ReserveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({ErrorCode.class,
        ReservedWithNullValueException.class,
        ReserveTimeConflictException.class,
        ReservedWithNoMatchValueException.class,
        UnsatisfiedLicenseException.class,
        ReserveService.class})
@WebMvcTest(controllers = ReserveController.class)
class ReserveControllerTest {

    @Mock
    ReserveService reserveService;

    @Autowired
    private MockMvc mockMvc;

    private CarManufacturer getCarManufacturer(){
        return new CarManufacturer();
    }

    private RepairMan getRepairMan(int level){
        return RepairMan.builder()
                .name("JAMES")
                .licenseId(level)
                .build();
    }
    private RepairShop getRepairShop(){
        return RepairShop.builder()
                .name("HI_MART")
                .build();
    }
    private User getUser(){
        return User.builder()
                .name("choiyt3465")
                .email("test@naver.com")
                .build();
    }
    private Car getCar(){
        return Car.builder()
                .name("SONATA")
                .userId(getUser())
                .carManufacturer(getCarManufacturer())
                .build();
    }

    private Reserve getReserve() {
        return  Reserve.builder()
                .car(new Car())
                .repairMan(new RepairMan())
                .repairShop(new RepairShop())
                .maintenanceItem(new MaintenanceItem())
                .startTime(LocalDateTime.now()).build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("정비공은 자기보다 높은 등급의 정비 항목을 선택할 수 없다.")
    void accessOpenWithAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/test").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andDo(print());
        Reserve reserve = getReserve();

    }

    @Test
    @WithAnonymousUser
    @DisplayName("인증되지 않은 사용자는 secured url 에 접속할 수 없다.")
    void accessSecuredWithAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/secured"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @DisplayName("인증된 사용자는 secured url 에 접속할 수 있다.")
    void accessSecuredWithUser() throws Exception {
        this.mockMvc.perform(get("/secured"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}