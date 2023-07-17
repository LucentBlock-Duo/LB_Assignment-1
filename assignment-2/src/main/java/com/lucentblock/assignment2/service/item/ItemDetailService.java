package com.lucentblock.assignment2.service.item;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.exception.ItemDetailNotFoundException;
import com.lucentblock.assignment2.repository.item.ItemDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ItemDetailService {
    private final ItemDetailRepository itemDetailRepository;
    public ItemDetail getItemByRepairManAndMaintenanceItem(RepairMan repairMan, MaintenanceItem maintenanceItem) {
        return itemDetailRepository.findByRepairManAndMaintenanceItem(repairMan, maintenanceItem)
                .orElseThrow(() -> new ItemDetailNotFoundException("Notfound"));
    }
    public ItemDetail getItemById(Long id) {
        return itemDetailRepository.findById(id).orElseThrow(() -> new ItemDetailNotFoundException(id.toString()));
    }
}
