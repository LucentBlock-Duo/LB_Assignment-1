package com.lucentblock.assignment2.service.maintenanceItem;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.maintenanceItem.ItemDetail;
import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import com.lucentblock.assignment2.exception.ItemDetailNotFoundException;
import com.lucentblock.assignment2.repository.maintenanceItem.ItemDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ItemDetailService {
    private final ItemDetailRepository repository;

    public List<ItemDetailDTO> getItemDetails() {
        return repository.findAll().stream()
                .map(itemdetail -> ItemDetail.toDTO(itemdetail))
                .collect(Collectors.toList());
    }

    public ItemDetailDTO getItemDetailByMaintenanceItemAndRepairMan(Long maintenanceItemId, Long repairManId) {
        return ItemDetail.toDTO(repository.findByMaintenanceItemIdAndRepairManIdAndDeletedAtIsNull(maintenanceItemId, repairManId)
                .orElseThrow(() -> new ItemDetailNotFoundException()));
    }

    public List<ItemDetailDTO> getItemDetailsByMaintenanceItemId(Long maintenanceItemId) {
        return repository.findAllByMaintenanceItemIdAndDeletedAtIsNull(maintenanceItemId).stream()
                .map(itemDetail -> ItemDetail.toDTO(itemDetail))
                .collect(Collectors.toList());
    }

    public List<ItemDetailDTO> getItemDetailsByRepairManId(Long repairManId) {
        return repository.findAllByRepairManIdAndDeletedAtIsNull(repairManId).stream()
                .map(itemDetail -> ItemDetail.toDTO(itemDetail))
                .collect(Collectors.toList());
    }
}
