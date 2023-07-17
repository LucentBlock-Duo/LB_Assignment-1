package com.lucentblock.assignment2.repository.item;

import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceItemRepository extends JpaRepository<MaintenanceItem, Long> {
    List<MaintenanceItem> findByIdIn(List<Long> ids);
}
