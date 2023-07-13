package com.lucentblock.assignment2.repository.maintenanceItem;

import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceItemRepository extends JpaRepository<MaintenanceItem, Long> {
}
