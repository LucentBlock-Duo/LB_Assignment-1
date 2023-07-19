package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.repository.RepairManRepository;
import com.lucentblock.assignment2.repository.ReserveRepository;
import com.lucentblock.assignment2.repository.item.ItemDetailRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class RepairManServiceTest {
    @Mock
    RepairManRepository repairManRepository;
    @Mock
    ReserveRepository reserveRepository;
    @Mock
    ItemDetailRepository itemDetailRepository;
    @InjectMocks
    RepairManService repairManService;

    @Test
    @DisplayName("")
    void fetchRepairManScheduleByDate() {
        given(reserveRepository.findReservesByRepairMan_IdAndDateAndDeletedAtIsNull(any(), any()))
                .willReturn(List.of(Reserve.builder()
                        .id(1L)
                        .startTime(LocalTime.of(9,0))
                        .endTime(LocalTime.of(9,30))
                        .date(LocalDate.now())
                        .car(Car.builder().build())
                        .repairMan(RepairMan.builder().build())
                        .repairShop(RepairShop.builder().build())
                        .itemDetail(ItemDetail.builder().build())
                        .createdAt(LocalDateTime.now())
                        .deletedAt(LocalDateTime.now())
                        .build()));

        Boolean[] booleans = new Boolean[18];
        Arrays.fill(booleans, true);
        booleans[0] = false;

        // when
        Boolean[] timeSlot = repairManService.fetchRepairManScheduleByDate(1L, LocalDate.now());

        // then
        Assertions.assertArrayEquals(booleans, timeSlot);
    }
}
