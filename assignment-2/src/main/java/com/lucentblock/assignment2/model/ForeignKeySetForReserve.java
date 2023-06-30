package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.MaintenanceItem;
import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.RepairShop;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@ToString
public class ForeignKeySetForReserve {
    Car car;
    RepairMan repairMan;
    RepairShop repairShop;
    MaintenanceItem maintenanceItem;

    public boolean isValidate(){
        return car!=null && repairMan!=null && repairShop!=null && maintenanceItem!=null;
    }
}
