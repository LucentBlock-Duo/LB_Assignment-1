package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RepairManRepository extends JpaRepository<RepairMan, Long> {

    @Query(value = "SELECT repair_man.* FROM repair_man INNER JOIN item_detail ON repair_man.id = item_detail.repair_man_id LEFT JOIN reserve ON repair_man.id = reserve.repair_man_id AND reserve.date = :date WHERE item_detail.maintenance_item_id = :maintenanceItemId AND (reserve.start_time IS NULL OR NOT (reserve.start_time < :endTime AND reserve.end_time > :startTime))", nativeQuery = true)
    List<RepairMan> findRepairMenAvailableAtDateTIme(@Param(value = "date")LocalDate date,
                                                     @Param(value = "startTime")LocalTime startTime,
                                                     @Param(value = "endTime")LocalTime endTime,
                                                     @Param(value = "maintenanceItemId")Long maintenanceItemId);
}
