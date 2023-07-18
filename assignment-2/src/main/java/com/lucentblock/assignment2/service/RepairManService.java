package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.exception.RepairManNotFoundException;
import com.lucentblock.assignment2.repository.RepairManRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RepairManService {
    private final RepairManRepository repairManRepository;

    public RepairMan getRepairManById(Long id) {
        return repairManRepository.findById(id).orElseThrow(() -> new RepairManNotFoundException(id.toString()));
    }
    public List<RepairMan> getAvailableRepairMenAtTimeAndMaintenanceItem(LocalDate date,
                                                                         LocalTime startTime,
                                                                         LocalTime endTime,
                                                                         Long maintenanceItemId) {
        return repairManRepository.findRepairMenAvailableAtDateTIme(date, startTime, endTime, maintenanceItemId);
    }

    public List<RepairMan> getRecommendRepairMenByUser(User user) {
        List<Long> recommendRepairMenIdsByUser = repairManRepository.findRecommendRepairMenIdsByUser(user);

        ArrayList<RepairMan> repairMen = new ArrayList<>();

        for (Long id : recommendRepairMenIdsByUser) {
            repairMen.add(repairManRepository.findById(id).orElseThrow(() -> new RepairManNotFoundException(id.toString())));
        }
        return repairMen;
    }
}
