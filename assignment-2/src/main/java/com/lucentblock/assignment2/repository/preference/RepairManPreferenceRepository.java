package com.lucentblock.assignment2.repository.preference;

import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import com.lucentblock.assignment2.entity.preference.RepairManPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairManPreferenceRepository extends JpaRepository<RepairManPreference, Long> {
    List<RepairManPreference> findAllByRepairManAndDeletedAtIsNull(RepairMan repairMan);
    List<RepairManPreference> findAllByMaintenanceItemAndDeletedAtIsNull(MaintenanceItem maintenanceItem);
    List<RepairManPreference> findAllByCarManufacturerAndDeletedAtIsNull(CarManufacturer carManufacturer);
}
