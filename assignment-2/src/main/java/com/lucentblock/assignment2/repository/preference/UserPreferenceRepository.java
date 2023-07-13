package com.lucentblock.assignment2.repository.preference;

import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import com.lucentblock.assignment2.entity.preference.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    List<UserPreference> findAllByUser(User user);
    List<UserPreference> findAllByCarManufacturer(CarManufacturer carManufacturer);
    List<UserPreference> findAllByMaintenanceItemAndDeletedAtIsNull(MaintenanceItem maintenanceItem);
}
