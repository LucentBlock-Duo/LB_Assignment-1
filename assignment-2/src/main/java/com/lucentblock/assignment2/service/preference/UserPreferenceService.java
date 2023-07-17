//package com.lucentblock.assignment2.service.preference;
//
//import com.lucentblock.assignment2.entity.car.CarManufacturer;
//import com.lucentblock.assignment2.entity.User;
//import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
//import com.lucentblock.assignment2.entity.preference.carManufacturer.UserCarManufacturerPreference;
//import com.lucentblock.assignment2.repository.preference.UserPreferenceRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service @RequiredArgsConstructor
//public class UserPreferenceService {
//    private final UserPreferenceRepository repository;
//
//    public List<UserCarManufacturerPreference> fetchPreferencesByUser(User user) {
//        return repository.findAllByUser(user);
//    }
//
//    public List<UserCarManufacturerPreference> fetchPreferencesByCarManufacturer(CarManufacturer carManufacturer) {
//        return repository.findAllByCarManufacturer(carManufacturer);
//    }
//
//    public List<UserCarManufacturerPreference> fetchPreferencesByMaintenanceItem(MaintenanceItem maintenanceItem) {
//        return repository.findAllByMaintenanceItemAndDeletedAtIsNull(maintenanceItem);
//    }
//}
