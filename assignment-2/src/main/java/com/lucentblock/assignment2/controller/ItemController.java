package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.model.maintenanceItem.ItemDetailDTO;
import com.lucentblock.assignment2.model.maintenanceItem.MaintenanceItemDTO;
import com.lucentblock.assignment2.service.RepairManService;
import com.lucentblock.assignment2.service.item.ItemDetailService;
import com.lucentblock.assignment2.service.item.MaintenanceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance_item")
@RequiredArgsConstructor
public class ItemController {
    private final MaintenanceItemService maintenanceItemService;
    private final ItemDetailService itemDetailService;
    private final RepairManService repairManService;

    @GetMapping
    public List<MaintenanceItemDTO> fetchMaintenanceItemList() {
        return maintenanceItemService.getAllItems().stream().map(item -> MaintenanceItem.toDTO(item)).toList();
    }

//    @GetMapping("/details")
//    public List<ItemDetailDTO> fetchItemDetails(@Valid @RequestBody List<ItemDetailQueryDTO> itemDetailQueryDTOs) {
//        return itemDetailQueryDTOs.stream()
//                .map( dto -> {
//                    MaintenanceItem maintenanceItem = maintenanceItemService.getItemById(dto.getMaintenanceItemId());
//                    RepairMan repairMan = repairManService.getRepairManById(dto.getRepairManId());
//                    return ItemDetail.toDTO(itemDetailService.getItemByRepairManAndMaintenanceItem(repairMan, maintenanceItem));
//                }).toList();
//    }
}
