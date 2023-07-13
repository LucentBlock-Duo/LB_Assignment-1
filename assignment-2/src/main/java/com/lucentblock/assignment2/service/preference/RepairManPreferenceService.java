package com.lucentblock.assignment2.service.preference;

import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import com.lucentblock.assignment2.entity.preference.RepairManPreference;
import com.lucentblock.assignment2.repository.preference.RepairManPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class RepairManPreferenceService {
    private final RepairManPreferenceRepository repository;

    public List<RepairManPreference> fetchPreferencesByRepairMan(RepairMan repairMan) {
        return repository.findAllByRepairMan(repairMan);
    }

    public List<RepairManPreference> fetchPreferencesByCarManufacturer(CarManufacturer carManufacturer) {
        return repository.findAllByCarManufacturer(carManufacturer);
    }

    public List<RepairManPreference> fetchPreferencesByMaintenanceItem(MaintenanceItem maintenanceItem) {
        return repository.findAllByMaintenanceItem(maintenanceItem);
    }
}
