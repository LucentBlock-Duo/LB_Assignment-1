package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.exception.ReserveErrorCode;
import com.lucentblock.assignment2.exception.ReserveNotFoundException;
import com.lucentblock.assignment2.exception.ReserveTimeConflictException;
import com.lucentblock.assignment2.model.RequestReserveReviewDTO;
import com.lucentblock.assignment2.model.ResponseReserveDTO;
import com.lucentblock.assignment2.repository.ReserveRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveService {
    private final PreviousRepairService previousRepairService;
    private final ReserveRepository reserveRepository;
    private final EntityManager em;

    public Reserve createReserve(RepairShop repairShop, Car car, LocalDate date, LocalTime startTime, ItemDetail item) {
        if (reserveRepository.findReservesByRepairManAndDeletedAtIsNull(startTime.plusMinutes(item.getMaintenanceItem().getRequiredTime()), startTime, item.getRepairMan()).size() > 0
        || reserveRepository.findReservesByCarAndDeletedAtIsNull(startTime.plusMinutes(item.getMaintenanceItem().getRequiredTime()), startTime, car).size() > 0) {
            throw new ReserveTimeConflictException(ReserveErrorCode.ERROR_102);
        }
        return reserveRepository.save(Reserve.builder()
                .car(car)
                .itemDetail(item)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(item.getMaintenanceItem().getRequiredTime()))
                .date(date)
                .repairShop(repairShop)
                .repairMan(item.getRepairMan())
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<Reserve> getReservesByUser(User user) {
        return reserveRepository.findReservesByUserAndDeletedAtIsNull(user);
    }

    public List<Reserve> deleteReserves(List<Long> reserveIds, String username) {
        List<Reserve> reserves = reserveRepository.findAllById(reserveIds);

        for (Reserve reserve : reserves) {
            if (!reserve.getCar().getUser().getEmail().equals(username)) {
                log.info(username + " 이 " + reserve.getCar().getUser().getEmail() + " 에 접근을 시도하였습니다.");
                throw new AccessDeniedException("잘못된 접근");
            }

            reserve.setDeletedAt(LocalDateTime.now());
            reserveRepository.save(reserve);
        }

        return reserves;
    }

    public ResponseReserveDTO setStatus(Long reserveId, Integer status){
        Reserve reserve=reserveRepository.findById(reserveId)
                .orElseThrow(()->new ReserveNotFoundException(ReserveErrorCode.ERROR_103));

        reserve.setStatus(status);

        if(RepairStatus.status(reserve.getStatus()).equals(RepairStatus.COMPLETED.status())){
            reserve.setEndTime(LocalTime.now());
            previousRepairService.createPreviousRepair(reserve.getId());
        } // status == complete 라면 정비 기록에 남긴다.

        return reserveRepository.save(reserve).toDto();
    }

    @Transactional
    public RepairMan evaluate(RequestReserveReviewDTO dto){
        RepairMan repairMan = em.find(RepairMan.class,dto.getRepair_man_id());
        Reserve reserve =reserveRepository.findById(dto.getReserve_id())
                .orElseThrow(()->new ReserveNotFoundException(ReserveErrorCode.ERROR_103));

        if(reserve.getIsReviewed()) throw new RuntimeException("이미 리뷰가 작성된 예약입니다.");

        int num=repairMan.getEvaluatedNum();
        double value=dto.getValue();
        double grade=repairMan.getEvaluationGrade();

        repairMan.setEvaluationGrade((grade*num+value)/(num+1));
        repairMan.setEvaluatedNum(num+1);

        reserve.setIsReviewed(true);

        return em.merge(repairMan);
    }
}
