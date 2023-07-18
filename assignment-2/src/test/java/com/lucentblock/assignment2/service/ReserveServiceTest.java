package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.exception.ReserveTimeConflictException;
import com.lucentblock.assignment2.repository.ReserveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReserveServiceTest {

    @Mock
    ReserveRepository reserveRepository;

    @InjectMocks
    ReserveService service;

    @Test
    @DisplayName("예약 가능한 수리공에게 예약 요청을 하면, 예약이 정상적으로 완료된다.")
    void createReserve() {
        // given
        RepairShop mockShop = mock(RepairShop.class);
        Car mockCar = mock(Car.class);
        LocalDate mockDate = mock(LocalDate.class);
        LocalTime mockStartTime = mock(LocalTime.class);
        ItemDetail mockItem = mock(ItemDetail.class);
        MaintenanceItem mockMaintenanceItem = mock(MaintenanceItem.class);
        given(mockMaintenanceItem.getRequiredTime()).willReturn(30);
        RepairMan mockRepairMan = mock(RepairMan.class);
        given(mockItem.getRepairMan()).willReturn(mockRepairMan);
        given(mockItem.getMaintenanceItem()).willReturn(mockMaintenanceItem);

        given(reserveRepository
                .findReservesByRepairManAndDeletedAtIsNull(any(), any(), any()))
                .willReturn(Collections.emptyList());
        given(reserveRepository.findReservesByCarAndDeletedAtIsNull(any(), any(), any()))
                .willReturn(Collections.emptyList());

        // when & then
        assertDoesNotThrow(() -> service.createReserve(mockShop, mockCar, mockDate, mockStartTime, mockItem));
    }

    @Test
    @DisplayName("수리공의 스케쥴이 겹치면, TimeConflict 예외를 발생시킨다.")
    void createReserveFailWithRepairManTimeConflict() {
        RepairShop mockShop = mock(RepairShop.class);
        Car mockCar = mock(Car.class);
        LocalDate mockDate = mock(LocalDate.class);
        LocalTime mockStartTime = mock(LocalTime.class);
        ItemDetail mockItem = mock(ItemDetail.class);
        MaintenanceItem mockMaintenanceItem = mock(MaintenanceItem.class);
        given(mockMaintenanceItem.getRequiredTime()).willReturn(30);
        RepairMan mockRepairMan = mock(RepairMan.class);
        given(mockItem.getRepairMan()).willReturn(mockRepairMan);
        given(mockItem.getMaintenanceItem()).willReturn(mockMaintenanceItem);

        given(reserveRepository
                .findReservesByRepairManAndDeletedAtIsNull(any(), any(), any()))
                .willReturn(List.of(Reserve.builder().build()));

        // when & then
        assertThrows(ReserveTimeConflictException.class, () -> service.createReserve(mockShop, mockCar, mockDate, mockStartTime, mockItem));
    }

    @Test
    @DisplayName("차량의 수리 스케쥴이 겹치면, TimeConflict 예외를 발생시킨다.")
    void createReserveFailWithCarTimeConflict() {
        RepairShop mockShop = mock(RepairShop.class);
        Car mockCar = mock(Car.class);
        LocalDate mockDate = mock(LocalDate.class);
        LocalTime mockStartTime = mock(LocalTime.class);
        ItemDetail mockItem = mock(ItemDetail.class);
        MaintenanceItem mockMaintenanceItem = mock(MaintenanceItem.class);
        given(mockMaintenanceItem.getRequiredTime()).willReturn(30);
        RepairMan mockRepairMan = mock(RepairMan.class);
        given(mockItem.getRepairMan()).willReturn(mockRepairMan);
        given(mockItem.getMaintenanceItem()).willReturn(mockMaintenanceItem);

        given(reserveRepository
                .findReservesByCarAndDeletedAtIsNull(any(), any(), any()))
                .willReturn(List.of(Reserve.builder().build()));

        // when & then
        assertThrows(ReserveTimeConflictException.class, () -> service.createReserve(mockShop, mockCar, mockDate, mockStartTime, mockItem));
    }

    @Test
    @DisplayName("로그인 한 유저의 예약 목록을 읽어올 수 있다.")
    void readReservesByUser() {
        // given
        User mockUser = mock(User.class);
        Reserve mockReserve = mock(Reserve.class);
        given(reserveRepository.findReservesByUserAndDeletedAtIsNull(mockUser)).willReturn(List.of(mockReserve));

        // when & then
        assertEquals(service.getReservesByUser(mockUser), List.of(mockReserve));
    }

    @Test
    @DisplayName("로그인 한 유저의 예약을 삭제할 수 있다.")
    void deleteReservesByUser() {
        // given
        User mockUser = mock(User.class);
        Car mockCar = mock(Car.class);
        Reserve mockReserve1 = mock(Reserve.class);
        Reserve mockReserve2 = mock(Reserve.class);

        given(mockUser.getEmail()).willReturn("mockUser@test.com");
        given(mockCar.getUser()).willReturn(mockUser);
        given(mockReserve1.getId()).willReturn(1L);
        given(mockReserve1.getCar()).willReturn(mockCar);
        given(mockReserve2.getId()).willReturn(2L);
        given(mockReserve2.getCar()).willReturn(mockCar);
        given(reserveRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(mockReserve1, mockReserve2));
        given(reserveRepository.save(mockReserve1)).willReturn(mockReserve1);
        given(reserveRepository.save(mockReserve2)).willReturn(mockReserve2);

        // when
        service.deleteReserves(List.of(mockReserve1.getId(), mockReserve2.getId()), "mockUser@test.com");

        // then
        verify(mockReserve1, times(1)).setDeletedAt(any(LocalDateTime.class));
        verify(mockReserve2, times(1)).setDeletedAt(any(LocalDateTime.class));
        verify(reserveRepository, times(2)).save(any(Reserve.class));
    }

    @Test
    @DisplayName("로그인 한 유저의 예약을 삭제할 수 있다.")
    void deleteReservesByUserDoesNotMatch() {
        // given
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        Car mockCar1 = mock(Car.class);
        Car mockCar2 = mock(Car.class);
        Reserve mockReserve1 = mock(Reserve.class);
        Reserve mockReserve2 = mock(Reserve.class);

        given(mockUser1.getEmail()).willReturn("mockUser@test.com");
        given(mockUser2.getEmail()).willReturn("doesNotMatch@test.com");
        given(mockCar1.getUser()).willReturn(mockUser1);
        given(mockCar2.getUser()).willReturn(mockUser2);
        given(mockReserve1.getCar()).willReturn(mockCar1);
        given(mockReserve2.getCar()).willReturn(mockCar2);
        given(reserveRepository.findAllById(any())).willReturn(List.of(mockReserve1, mockReserve2));

        // when & then
        assertThrows(AccessDeniedException.class,
                () -> service.deleteReserves(
                        List.of(mockReserve1.getId(), mockReserve2.getId()),
                        "mockUser@test.com"));
    }
}
