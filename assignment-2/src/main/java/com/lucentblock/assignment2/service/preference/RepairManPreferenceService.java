//package com.lucentblock.assignment2.service.preference;
//
//import com.lucentblock.assignment2.entity.car.CarManufacturer;
//import com.lucentblock.assignment2.entity.RepairMan;
//import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
//import com.lucentblock.assignment2.entity.preference.carManufacturer.RepairManCarManufacturerPreference;
//import com.lucentblock.assignment2.repository.preference.RepairManPreferenceRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service @RequiredArgsConstructor
//public class RepairManPreferenceService {
//    private final RepairManPreferenceRepository repository;
//
//    public List<RepairManCarManufacturerPreference> fetchPreferencesByRepairMan(RepairMan repairMan) {
//        return repository.findAllByRepairManAndDeletedAtIsNull(repairMan);
//    }
//
//    public List<RepairManCarManufacturerPreference> fetchPreferencesByCarManufacturer(CarManufacturer carManufacturer) {
//        return repository.findAllByCarManufacturerAndDeletedAtIsNull(carManufacturer);
//    }
//
//    public List<RepairManCarManufacturerPreference> fetchPreferencesByMaintenanceItem(MaintenanceItem maintenanceItem) {
//        return repository.findAllByMaintenanceItemAndDeletedAtIsNull(maintenanceItem);
//    }
//}
