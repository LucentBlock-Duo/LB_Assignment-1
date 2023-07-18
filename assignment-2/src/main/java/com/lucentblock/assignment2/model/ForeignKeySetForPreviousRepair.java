package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.exception.ReserveErrorCode;
import com.lucentblock.assignment2.exception.ReservedWithNoMatchValueException;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Builder
@AllArgsConstructor
@ToString
public class ForeignKeySetForPreviousRepair {
    User user;
    Car car;
    RepairMan repairMan;
    RepairShop repairShop;
    MaintenanceItem maintenanceItem;
}
