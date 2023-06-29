package com.lucentblock.assignment2;

import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.model.CreateRequestReserveDTO;
import com.lucentblock.assignment2.model.ForeignKeySetForReserve;
import com.lucentblock.assignment2.model.RequestReserveDTO;
import com.lucentblock.assignment2.repository.ReserveRepository;
import com.lucentblock.assignment2.service.ReserveService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@SpringBootTest
class ApplicationTests {
    private final ReserveService reserveService;
    private final ReserveRepository reserveRepository;
    private final EntityManager em;
    ApplicationTests(ReserveService reserveService, ReserveRepository reserveRepository, EntityManager em) {
        this.reserveService = reserveService;
        this.reserveRepository = reserveRepository;
        this.em = em;
    }


    @Test
    private void timeConflictTest() {
        Long car_id=1L;
        Long repair_shop_id=1L;
        Long repair_man_id=1L;
        Long maintenance_item_id=1L;
        LocalDateTime start_time=LocalDateTime.now();

        CreateRequestReserveDTO dto=
                new CreateRequestReserveDTO(car_id,repair_shop_id,repair_man_id,maintenance_item_id,start_time);

        ForeignKeySetForReserve foreignKeySet=getForeignKeySet(dto);
        Reserve reserve = dto.toEntity(foreignKeySet);

        Assert.isTrue(foreignKeySet.isValidate() && ableToReserve(reserve),
                "Time Conflict");

        System.out.println("No Problem");
    }

    private ForeignKeySetForReserve getForeignKeySet(RequestReserveDTO dto){
        Car car = em.find(Car.class, dto.getCar_id());
        RepairMan repairMan = em.find(RepairMan.class, dto.getRepair_man_id());
        RepairShop repairShop = em.find(RepairShop.class, dto.getRepair_shop_id());
        MaintenanceItem maintenanceItem = em.find(MaintenanceItem.class, dto.getMaintenance_item_id());


        return ForeignKeySetForReserve.builder().car(car).
                repairMan(repairMan).
                repairShop(repairShop).
                maintenanceItem(maintenanceItem).build();
    }
    private boolean ableToReserve(Reserve givenReserve) {
        for (Reserve reserve : reserveRepository.findReservesByRepairMan(givenReserve.getRepairMan())) {
            if (isTimeConflict(reserve, givenReserve)) return false;
        } // 정비공에 대한 스케줄 충돌 확인

        for (Reserve reserve : reserveRepository.findReservesByCar(givenReserve.getCar())) {
            if (isTimeConflict(reserve, givenReserve)) return false;
        } // 차에 대한 스케줄 충돌 확인

        return true;
    }

    // TimeConflict의 조건
    // 1. 해당 정비공이 이미 예약이 잡혀있거나
    // 2. 해당 차가 이미 예약이 잡혀있거나

    private boolean isTimeConflict(Reserve base, Reserve inner) {
        LocalDateTime[] basePeriod = {base.getStartTime(), base.getEndTime()};
        LocalDateTime[] innerPeriod = {inner.getStartTime(), inner.getEndTime()};

        return !base.getId().equals(inner.getId())&&
                (innerPeriod[0].isAfter(basePeriod[0]) && innerPeriod[0].isBefore(basePeriod[1]) ||
                        innerPeriod[1].isAfter(basePeriod[0]) && innerPeriod[1].isBefore(basePeriod[1]) ||
                        basePeriod[0].isEqual(innerPeriod[0]) || basePeriod[1].isEqual(innerPeriod[0]) ||
                        basePeriod[0].isEqual(innerPeriod[1]) || basePeriod[1].isEqual(innerPeriod[1]));
    } // 두 예약에 대한 시간 충돌 여부

}
