package com.lucentblock.assignment2.repository;


import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve,Long> {

    /*
        신규 예약 요청이 들어왔을 때, 기존 예약 중에 신규 예약 요청된 수리공의 스케쥴이 겹치지는 않는지 확인하고
        기존 예약 중에 신규 예약 요청된 시간이 신규 예약 요청된 차량이 갖고있는 기존 예약 스케쥴과 겹치는지 확인 (CreateReserve)
     */
    @Query(value = "select * from assignment.reserve where repair_man_id = :#{#repairMan.id} and (start_time < :endTime and end_time > :startTime)"
    , nativeQuery = true)
    List<Reserve> findReservesByRepairManAndDeletedAtIsNull(LocalTime endTime, LocalTime startTime, RepairMan repairMan);

    @Query(value = "select * from assignment.reserve where car_id = :#{#car.id} and (start_time < :endTime and end_time > :startTime)"
            , nativeQuery = true)
    List<Reserve> findReservesByCarAndDeletedAtIsNull(LocalTime endTime, LocalTime startTime, Car car);

    @Query(value = "SELECT reserve.*" +
            "  FROM assignment.reserve JOIN car ON reserve.car_id = car.id WHERE car.user_id = :#{#user.id} AND reserve.deleted_at IS NULL", nativeQuery = true)
    List<Reserve> findReservesByUserAndDeletedAtIsNull(User user);
    List<Reserve> findReservesByRepairManAndDateAndStartTimeLessThanAndEndTimeGreaterThanAndDeletedAtIsNull(RepairMan repairMan, LocalDate date, LocalTime newEndTime, LocalTime newStartTime);
    List<Reserve> findReservesByRepairMan_IdAndDateAndDeletedAtIsNull(Long repairManId, LocalDate date);
}
