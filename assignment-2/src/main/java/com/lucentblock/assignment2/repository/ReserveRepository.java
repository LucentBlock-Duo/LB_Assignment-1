package com.lucentblock.assignment2.repository;


import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve,Long> {
    List<Reserve> findReservesByCarAndDeletedAtIsNull(Car car); // Car에 대한 예약 리스트 가져오기
    List<Reserve> findReservesByRepairManAndDeletedAtIsNull(RepairMan repairMan); // RepairMan에 대한 대한 예약 리스트 가져오기

    default List<Reserve> findReservesByRepairMan(RepairMan repairMan){
        return findReservesByRepairManAndDeletedAtIsNull(repairMan);
    } // deleted At Null Checking

    default List<Reserve> findReservesByCar(Car car){
        return findReservesByCarAndDeletedAtIsNull(car);
    } // deleted At Null Checking
    List<Reserve> findReservesByRepairManOrCarAndDeletedAtIsNull(RepairMan repairMan, Car car);
}
