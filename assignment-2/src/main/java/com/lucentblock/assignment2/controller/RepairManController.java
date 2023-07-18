package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.model.RepairManInfoDTO;
import com.lucentblock.assignment2.service.RepairManService;
import com.lucentblock.assignment2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController @RequestMapping("/api/repair_man")
@Slf4j @RequiredArgsConstructor
public class RepairManController {
    private final RepairManService repairManService;
    private final UserService userService;

    @GetMapping("/recommend")
    public ResponseEntity fetchRecommendedRepairMen() {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(repairManService.getRecommendRepairMenByUser(user));
    }

    @GetMapping(value = "/available", params = {"date", "start_time"})
    public List<RepairManInfoDTO> fetchRepairManAvailableAtDateAndTime(@RequestParam("date")LocalDate date, @RequestParam("start_time")LocalTime startTime) {
        return repairManService.fetchRepairManAvailableAtDateAndTime(date, startTime);
    }

    @GetMapping(value = "/available", params = {"maintenance_item_id"})
    public List<RepairManInfoDTO> fetchRepairManAvailableByItem(@RequestParam("maintenance_item_id")Long maintenanceItemId) {
        return repairManService.fetchRepairManAvailableByItem(maintenanceItemId);
    }

    @GetMapping(value = "/available", params = {"date", "start_time", "repair_men_ids"})
    public List<RepairManInfoDTO> filterRepairMenByDateTime(@RequestParam("date")LocalDate date, @RequestParam("start_time")LocalTime startTime, @RequestParam("repair_men_ids")List<Long> ids) {
        return repairManService.filterRepairMenByDateTime(date, startTime, ids);
    }

    @GetMapping(value = "/available", params = {"maintenance_item_id", "repair_men_ids"})
    public List<RepairManInfoDTO> filterRepairMenByItem(@RequestParam("maintenance_item_id")Long maintenanceItemId, @RequestParam("repair_man_ids")List<Long> ids) {
        return repairManService.filterRepairMenByItem(maintenanceItemId, ids);
    }

    @GetMapping(value = "/available", params = {"repair_man_id", "date"})
    public Boolean[] fetchRepairManScheduleByDate(@RequestParam("repair_man_id")Long repairManId, @RequestParam("date")LocalDate date) {
        return repairManService.fetchRepairManScheduleByDate(repairManId, date);
    }
}
