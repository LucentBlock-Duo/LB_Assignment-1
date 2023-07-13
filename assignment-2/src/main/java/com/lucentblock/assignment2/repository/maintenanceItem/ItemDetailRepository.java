package com.lucentblock.assignment2.repository.maintenanceItem;

import com.lucentblock.assignment2.entity.maintenanceItem.ItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemDetailRepository extends JpaRepository<ItemDetail, Long> {
    Optional<ItemDetail> findByMaintenanceItemIdAndRepairManIdAndDeletedAtIsNull(Long maintenanceItemId, Long repairManId);
    List<ItemDetail> findAllByMaintenanceItemIdAndDeletedAtIsNull(Long maintenanceItemId);
    List<ItemDetail> findAllByRepairManIdAndDeletedAtIsNull(Long repairManId);
}
