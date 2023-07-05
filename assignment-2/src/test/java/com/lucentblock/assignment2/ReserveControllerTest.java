package com.lucentblock.assignment2;

import com.lucentblock.assignment2.exception.ReserveTimeConflictException;
import com.lucentblock.assignment2.exception.ReservedWithNoMatchValueException;
import com.lucentblock.assignment2.exception.UnsatisfiedLicenseException;
import com.lucentblock.assignment2.model.CreateRequestReserveDTO;
import com.lucentblock.assignment2.service.ReserveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;



@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest
class ReserveControllerTest {

    @Autowired
    private ReserveService reserveService;
  
    @Test
    @DisplayName("정비공은 자기보다 높은 등급의 정비 항목을 선택할 수 없다.")
    void createReservationWithUnsatisfiedException() {
        // given
        CreateRequestReserveDTO dto=
                CreateRequestReserveDTO.builder()
                        .car_id(1L)
                        .repair_man_id(1L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(3L)
                        .build();

        // when, then
        assertThatThrownBy(() -> reserveService.createReserve(dto))
                .isInstanceOf(UnsatisfiedLicenseException.class);
    }

    @Test
    @DisplayName("예약에 필요한 자동차 정보를 보유하고 있지 않다면 예약할 수 없다.")
    void createReservationWithNoMatchValueException() {
        // given
        CreateRequestReserveDTO dto=
                CreateRequestReserveDTO.builder()
                        .car_id(10L)
                        .repair_man_id(6L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(2L)
                        .build();

        // when, then
        assertThatThrownBy(() -> reserveService.createReserve(dto))
                .isInstanceOf(ReservedWithNoMatchValueException.class);
    }

    @Test
    @DisplayName("같은 차량에 대해서 기존 예약과 시간이 겹치면 예약할 수 없다.")
    void createReservationWithCarTimeConflictException() {
        // given
        long carId=1L;
        CreateRequestReserveDTO dto1=
                CreateRequestReserveDTO.builder()
                        .car_id(carId)
                        .repair_man_id(1L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(2L)
                        .build();
        CreateRequestReserveDTO dto2=
                CreateRequestReserveDTO.builder()
                        .car_id(carId)
                        .repair_man_id(1L)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(2L)
                        .build();

        // when, then
        reserveService.createReserve(dto1);

        assertThatThrownBy(() -> reserveService.createReserve(dto2))
                .isInstanceOf(ReserveTimeConflictException.class);
    }

    @Test
    @DisplayName("같은 정비공에 대해서 기존 예약과 시간이 겹치면 예약할 수 없다.")
    void createReservationWithRepairManTimeConflictException() {
        // given
        long repairManId=1L;
        CreateRequestReserveDTO dto1=
                CreateRequestReserveDTO.builder()
                        .car_id(1L)
                        .repair_man_id(repairManId)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(1L)
                        .build();
        CreateRequestReserveDTO dto2=
                CreateRequestReserveDTO.builder()
                        .car_id(2L)
                        .repair_man_id(repairManId)
                        .repair_shop_id(1L)
                        .start_time(LocalDateTime.now())
                        .maintenance_item_id(2L)
                        .build();

        // when, then
        reserveService.createReserve(dto1);
        assertThatThrownBy(() -> reserveService.createReserve(dto2))
                .isInstanceOf(ReserveTimeConflictException.class);
    }
}