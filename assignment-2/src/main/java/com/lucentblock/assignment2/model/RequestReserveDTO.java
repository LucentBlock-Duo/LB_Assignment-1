package com.lucentblock.assignment2.model;

import com.lucentblock.assignment2.entity.Car;
import com.lucentblock.assignment2.entity.MaintenanceItem;
import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.RepairShop;

import java.time.LocalDateTime;

public interface RequestReserveDTO {
    Long getCar_id(); // 차
    Long getRepair_shop_id(); // 정비소
    Long getRepair_man_id(); // 정비공
    Long getMaintenance_item_id(); // 정비항목
    LocalDateTime getStart_time(); // 예약시작
    boolean isValid();
}
