package com.lucentblock.assignment2.repository.item;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemDetailRepository extends JpaRepository<ItemDetail, Long> {
    Optional<ItemDetail> findByRepairManAndMaintenanceItem(RepairMan repairMan, MaintenanceItem maintenanceItem);
}