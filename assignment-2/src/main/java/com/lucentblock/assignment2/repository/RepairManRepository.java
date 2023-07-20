package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RepairManRepository extends JpaRepository<RepairMan, Long> {

    List<RepairMan> findByNameContainingAndDeletedAtIsNull(String name);

    @Query(value = "SELECT repair_man.* FROM repair_man INNER JOIN item_detail ON repair_man.id = item_detail.repair_man_id LEFT JOIN reserve ON repair_man.id = reserve.repair_man_id AND reserve.date = :date WHERE item_detail.maintenance_item_id = :maintenanceItemId AND (reserve.start_time IS NULL OR NOT (reserve.start_time < :endTime AND reserve.end_time > :startTime))", nativeQuery = true)
    List<RepairMan> findRepairMenAvailableAtDateTIme(@Param(value = "date")LocalDate date,
                                                     @Param(value = "startTime")LocalTime startTime,
                                                     @Param(value = "endTime")LocalTime endTime,
                                                     @Param(value = "maintenanceItemId")Long maintenanceItemId);

    @Query(value = "SELECT repair_man_id, " +
            "CASE WHEN brand_match = 1 AND item_match = 1 THEN 1 " +
            "WHEN brand_match = 1 THEN 2 " +
            "WHEN item_match = 1 THEN 3 " +
            "ELSE 4 END AS preference_score " +
            "FROM " +
            "(SELECT rbp.repair_man_id, " +
            "MAX(CASE WHEN ubp.car_manufacturer_id IS NOT NULL THEN 1 ELSE 0 END) as brand_match, " +
            "MAX(CASE WHEN uip.maintenance_item_id IS NOT NULL THEN 1 ELSE 0 END) as item_match " +
            "FROM repair_man_brand_preference rbp " +
            "LEFT JOIN user_brand_preference ubp ON rbp.car_manufacturer_id = ubp.car_manufacturer_id AND ubp.user_id = :#{#user.id} " +
            "LEFT JOIN repair_man_item_preference rip ON rbp.repair_man_id = rip.repair_man_id " +
            "LEFT JOIN user_item_preference uip ON rip.maintenance_item_id = uip.maintenance_item_id AND uip.user_id = :#{#user.id} " +
            "GROUP BY rbp.repair_man_id) AS repairman_preference ORDER BY preference_score ASC", nativeQuery = true)
    List<Long> findRecommendRepairMenIdsByUser(@Param(value = "user") User user);
}
