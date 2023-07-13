package com.lucentblock.assignment2.controller.maintenanceItem;

import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import com.lucentblock.assignment2.model.maintenanceItem.MaintenanceItemDTO;
import com.lucentblock.assignment2.service.maintenanceItem.ItemDetailDTO;
import com.lucentblock.assignment2.service.maintenanceItem.ItemDetailService;
import com.lucentblock.assignment2.service.maintenanceItem.MaintenanceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/maintenanceItem")
@RequiredArgsConstructor
public class MaintenanceItemController {

    private final MaintenanceItemService maintenanceItemService;
    private final ItemDetailService itemDetailService;

    @GetMapping
    public List<MaintenanceItemDTO> fetchItems() {
        return maintenanceItemService.fetchItems().stream()
                .map(maintenanceItem -> MaintenanceItem.toDTO(maintenanceItem))
                .collect(Collectors.toList());
    }

    @GetMapping("/details")
    public List<ItemDetailDTO> fetchItemDetails() {
        return itemDetailService.getItemDetails();
    }

    @GetMapping(value = "/details", params = {"maintenance_item_id", "repair_man_id"})
    public ItemDetailDTO fetchItemDetailByMaintenanceItemIdAndRepairManId(Long maintenanceItemId, Long repairManId) {
        return itemDetailService.getItemDetailByMaintenanceItemAndRepairMan(maintenanceItemId, repairManId);
    }

    @GetMapping(value = "/details", params = "maintenance_item_id")
    public List<ItemDetailDTO> fetchItemDetailByMaintenanceItemId(@RequestParam(name = "maintenance_item_id") Long maintenanceItemId) {
        return itemDetailService.getItemDetailsByMaintenanceItemId(maintenanceItemId);
    }

    @GetMapping(value = "/details", params = "repair_man_id")
    public List<ItemDetailDTO> fetchItemDetailByRepairManId(@RequestParam(name = "repair_man_id") Long repairManId) {
        return itemDetailService.getItemDetailsByRepairManId(repairManId);
    }
}
