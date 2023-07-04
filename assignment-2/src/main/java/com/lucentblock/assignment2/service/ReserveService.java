package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.model.*;
import com.lucentblock.assignment2.repository.ReserveRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ReserveService {
    private final EntityManager em;
    private final ReserveRepository reserveRepository;

    public ReserveService(EntityManager em, ReserveRepository reserveRepository) {
        this.em = em;
        this.reserveRepository = reserveRepository;
    }

    public List<Reserve> findReserveByCarId(long carId) {
        return reserveRepository.findReservesByCar(em.find(Car.class,carId));
    } // 차에 대한 예약 리스트 가져오기

    public ResponseReserveDTO updateReserve(UpdateRequestReserveDTO dto){
        Reserve reserve =reserveRepository.findById(dto.getReserve_id()).orElse(null);

        if(reserve==null){
            log.info("Reserve not found At {}",Thread.currentThread().getStackTrace()[1].getMethodName());
            return null;
        }

        ForeignKeySetForReserve foreignKeySet=getForeignKeySet(dto);

        reserve.setCar(foreignKeySet.getCar());
        reserve.setMaintenanceItem(foreignKeySet.getMaintenanceItem());
        reserve.setRepairMan(foreignKeySet.getRepairMan());
        reserve.setRepairShop(foreignKeySet.getRepairShop());
        reserve.setStartTime(dto.getStart_time());
        reserve.setEndTime(dto.getStart_time().
                plusMinutes(foreignKeySet.getMaintenanceItem().getRequiredTime()));
        // Transaction을 적용해야 set이 반영 안되려나?

        if(foreignKeySet.isValidate())
            return reserve.toFailureDto(101);
        else if(ableToReserve(reserve))
            return reserve.toFailureDto(103);
        else if(ableToRepair(reserve))
            return reserve.toFailureDto(102);

        return reserveRepository.save(reserve).toSuccessDto();
    }

    private boolean ableToRepair(Reserve reserve){
        return reserve.getRepairMan().getLicenseId()
                >= reserve.getMaintenanceItem().getRequiredLicense();
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

    public ResponseReserveDTO createReserve(CreateRequestReserveDTO dto) {
        ForeignKeySetForReserve foreignKeySet=getForeignKeySet(dto);
        Reserve reserve = dto.toEntity(foreignKeySet);

        if(foreignKeySet.isValidate())
            return reserve.toFailureDto(101);
        else if(ableToReserve(reserve))
            return reserve.toFailureDto(103);
        else if(ableToRepair(reserve))
            return reserve.toFailureDto(102);

        return reserveRepository.save(reserve).toSuccessDto();
    }


    private boolean ableToReserve(Reserve givenReserve) {
        for (Reserve reserve : reserveRepository.findAbleReserves(givenReserve.getRepairMan(),givenReserve.getCar())) {
            if (isTimeConflict(reserve, givenReserve)) return false;
        } // 차와 정비공에 대한 스케줄 충돌 확인

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

    public Reserve deleteReserve(Long reserveId){
        Reserve reserve = reserveRepository.findById(reserveId).orElse(null);

        if(reserve!=null){
            reserve.delete();
            reserveRepository.save(reserve);
        }

        return reserve;
    }

}
