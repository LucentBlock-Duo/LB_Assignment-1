package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.exception.*;
import com.lucentblock.assignment2.model.*;
import com.lucentblock.assignment2.repository.ReserveRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveService {

    private final EntityManager em;
    private final ReserveRepository reserveRepository;

    public List<Reserve> findReserveByCarId(long carId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Car car = em.find(Car.class, carId);

        if (!currentUser.equals(car.getUser().getEmail())) {
            log.info(currentUser + " 이 " + car.getUser().getEmail() + " 의 예약 정보 조회를 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }
        return reserveRepository.findReservesByCar(car);
    } // 차에 대한 예약 리스트 가져오기

    @Transactional
    public ResponseReserveDTO updateReserve(UpdateRequestReserveDTO dto) throws RuntimeException{
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        Reserve reserve =reserveRepository.findById(dto.getReserve_id())
                .orElseThrow(()->new ReserveNotFoundException(ReserveErrorCode.ERROR_103));

        if (!currentUser.equals(reserve.getCar().getUser().getEmail())) {
            log.info(currentUser + " 이 " + reserve.getCar().getUser().getEmail() + " 의 예약 정보 변경을 시도했습니다.");
            throw new AccessDeniedException("잘못된 접근");
        }
        ForeignKeySetForReserve foreignKeySet=getForeignKeySet(dto);

        reserve.setCar(foreignKeySet.getCar());
        reserve.setMaintenanceItem(foreignKeySet.getMaintenanceItem());
        reserve.setRepairMan(foreignKeySet.getRepairMan());
        reserve.setRepairShop(foreignKeySet.getRepairShop());
        reserve.setStartTime(dto.getStart_time());
        reserve.setEndTime(dto.getStart_time().
                plusMinutes(foreignKeySet.getMaintenanceItem().getRequiredTime()));

        checkingValidationForReserve(reserve,foreignKeySet); // 예외상황 check

        return reserveRepository.save(reserve).toDto();
    }

    private void checkingValidationForReserve(Reserve reserve,ForeignKeySetForReserve set) throws RuntimeException{
        set.isValidate();
        checkAbleToRepair(reserve);
        checkAbleToReserve(reserve);
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

    public ResponseReserveDTO createReserve(CreateRequestReserveDTO dto) throws RuntimeException {
        ForeignKeySetForReserve foreignKeySet=getForeignKeySet(dto);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUsername.equals(foreignKeySet.getCar().getUser().getEmail())) {
            log.info(currentUsername + " 이 " + foreignKeySet.getCar().getUser().getEmail() + " 으로 예약 생성을 시도하였습니다.");
            throw new AccessDeniedException("허용되지 않은 접근");
        }
        Reserve reserve = dto.toEntity(foreignKeySet);

        checkingValidationForReserve(reserve,foreignKeySet); // 예외상황 check
        return reserveRepository.save(reserve).toDto();
    }

    @Transactional
    public void checkAbleToReserve(Reserve givenReserve) {
        for (Reserve reserve : reserveRepository.findAbleReserves(givenReserve.getRepairMan(),givenReserve.getCar())) {
            if (isTimeConflict(reserve, givenReserve))
                throw new ReserveTimeConflictException(ReserveErrorCode.ERROR_102);
        } // 차와 정비공에 대한 스케줄 충돌 확인
    }

    private boolean isTimeConflict(Reserve base, Reserve inner) {
        LocalDateTime[] basePeriod = {base.getStartTime(), base.getEndTime()};
        LocalDateTime[] innerPeriod = {inner.getStartTime(), inner.getEndTime()};

        return !base.getId().equals(inner.getId())&&
                (innerPeriod[0].isAfter(basePeriod[0]) && innerPeriod[0].isBefore(basePeriod[1]) ||
                innerPeriod[1].isAfter(basePeriod[0]) && innerPeriod[1].isBefore(basePeriod[1]) ||
                basePeriod[0].isEqual(innerPeriod[0]) || basePeriod[1].isEqual(innerPeriod[0]) ||
                basePeriod[0].isEqual(innerPeriod[1]) || basePeriod[1].isEqual(innerPeriod[1]));
    } // 두 예약에 대한 시간 충돌 여부

    private void checkAbleToRepair(Reserve reserve){
        if(reserve.getRepairMan().getLicenseId() < reserve.getMaintenanceItem().getRequiredLicense())
            throw new UnsatisfiedLicenseException(ReserveErrorCode.ERROR_104,reserve);
    }

    @Transactional
    public Reserve deleteReserve(Long reserveId){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(()->new ReserveNotFoundException(ReserveErrorCode.ERROR_103));

        if (!currentUsername.equals(reserve.getCar().getUser().getEmail())) {
            log.info(currentUsername + " 이 " + reserve.getCar().getUser().getEmail() + " 으로 예약 삭제를 시도하였습니다.");
            throw new AccessDeniedException("허용되지 않은 접근");
        }

        reserve.delete();
        reserveRepository.save(reserve);

        return reserve;
    }
}
