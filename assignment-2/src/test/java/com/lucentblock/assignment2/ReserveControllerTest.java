package com.lucentblock.assignment2;

import com.lucentblock.assignment2.exception.ReserveTimeConflictException;
import com.lucentblock.assignment2.exception.ReservedWithNoMatchValueException;
import com.lucentblock.assignment2.exception.UnsatisfiedLicenseException;
import com.lucentblock.assignment2.model.CreateRequestReserveDTO;
import com.lucentblock.assignment2.repository.ReserveRepository;
import com.lucentblock.assignment2.service.ReserveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;



@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest
class ReserveControllerTest {

    @Autowired
    private ReserveService reserveService;

    @Mock
    private ReserveRepository reserveRepository;

    @Test
    @DisplayName("정비공은 자기보다 높은 등급의 정비 항목을 선택할 수 없다.")
    @WithMockUser(username = "ilmo@gmail.com", authorities = "ROLE_USER")
    void createReservationWithUnsatisfiedException() {
        // given
        CreateRequestReserveDTO dto=
                CreateRequestReserveDTO.builder()
                        .car_id(3L)
                        .repair_man_id(1L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(8L)
                        .build();

        // when, then
        assertThatThrownBy(() -> reserveService.createReserve(dto))
                .isInstanceOf(UnsatisfiedLicenseException.class);
    }

    @Test
    @DisplayName("예약에 필요한 자동차 정보를 보유하고 있지 않다면 예약할 수 없다.")
    @WithMockUser(username = "ilmo@gmail.com", authorities = "ROLE_USER")
    void createReservationWithNoMatchValueException() {
        // given
        CreateRequestReserveDTO dto=
                CreateRequestReserveDTO.builder()
                        .car_id(10L)
                        .repair_man_id(1L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(8L)
                        .build();

        // when, then
        assertThatThrownBy(() -> reserveService.createReserve(dto))
                .isInstanceOf(ReservedWithNoMatchValueException.class);
    }

    @Test
    @DisplayName("같은 차량에 대해서 기존 예약과 시간이 겹치면 예약할 수 없다.")
    @WithMockUser(username = "ilmo@gmail.com", authorities = "ROLE_USER")
    void createReservationWithCarTimeConflictException() {
        // given
        long carId=3L;
        CreateRequestReserveDTO dto1=
                CreateRequestReserveDTO.builder()
                        .car_id(carId)
                        .repair_man_id(3L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(8L)
                        .build();
        CreateRequestReserveDTO dto2=
                CreateRequestReserveDTO.builder()
                        .car_id(carId)
                        .repair_man_id(3L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(7L)
                        .build();

        // when, then
        assertThatThrownBy(() -> reserveService.createReserve(dto1))
                .isInstanceOf(ReserveTimeConflictException.class);
        assertThatThrownBy(() -> reserveService.createReserve(dto2))
                .isInstanceOf(ReserveTimeConflictException.class);
    }

    @Test
    @DisplayName("같은 정비공에 대해서 기존 예약과 시간이 겹치면 예약할 수 없다.")
    @WithMockUser(username = "ilmo@gmail.com", authorities = "ROLE_USER")
    void createReservationWithRepairManTimeConflictException() {
        // given
        long repairManId=1L;
        CreateRequestReserveDTO dto1=
                CreateRequestReserveDTO.builder()
                        .car_id(3L)
                        .repair_man_id(repairManId)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(7L)
                        .build();
        CreateRequestReserveDTO dto2=
                CreateRequestReserveDTO.builder()
                        .car_id(5L)
                        .repair_man_id(repairManId)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(7L)
                        .build();

        // when, then
        assertThatThrownBy(() -> reserveService.createReserve(dto1))
                .isInstanceOf(ReserveTimeConflictException.class);
        assertThatThrownBy(() -> reserveService.createReserve(dto2))
                .isInstanceOf(ReserveTimeConflictException.class);
    }
}