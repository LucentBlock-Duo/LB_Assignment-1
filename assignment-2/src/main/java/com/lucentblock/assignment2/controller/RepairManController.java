package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.model.RepairmanAvailabilityQueryDTO;
import com.lucentblock.assignment2.service.RepairManService;
import com.lucentblock.assignment2.service.item.ItemDetailDTO;
import com.lucentblock.assignment2.service.item.ItemDetailService;
import com.lucentblock.assignment2.service.item.MaintenanceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController @RequestMapping("/api/repair_man")
@Slf4j @RequiredArgsConstructor
public class RepairManController {
    private final RepairManService repairManService;
    private final MaintenanceItemService maintenanceItemService;
    private final ItemDetailService itemDetailService;

    @GetMapping("/available")
    public List<ItemDetailDTO> fetchRepairManAvailableAtDateTime(@Valid @RequestBody RepairmanAvailabilityQueryDTO queryDTO) {
        MaintenanceItem item = maintenanceItemService.getItemById(queryDTO.getMaintenanceItemId());
        LocalTime endTime = queryDTO.getStartTime()
                .plusMinutes(item.getRequiredTime());

        return repairManService.getAvailableRepairMenAtTimeAndMaintenanceItem(queryDTO.getDate(), queryDTO.getStartTime(), endTime, queryDTO.getMaintenanceItemId()).stream()
                .map(repairMan -> ItemDetail.toDTO(itemDetailService.getItemByRepairManAndMaintenanceItem(repairMan, item)))
                .sorted()
                .toList();
    }
}
