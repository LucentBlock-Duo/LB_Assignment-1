package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UpdateRequestReserveDTO implements RequestReserveDTO{
    Long reserve_id; // 예약 아이디
    Long car_id; // 차
    Long repair_shop_id; // 정비소
    Long repair_man_id; // 정비공
    Long maintenance_item_id; // 정비항목
    LocalDateTime start_time; // 예약시작

    public boolean isValidate(){
        return reserve_id!=null && car_id!=null && repair_shop_id!=null && repair_man_id!=null &&
                maintenance_item_id !=null && start_time!=null;
    }

    public Reserve toEntity(Car car, RepairMan repairMan, RepairShop repairShop,MaintenanceItem maintenanceItem){
        return Reserve.builder().
                car(car).
                startTime(start_time).
                endTime(start_time.plusMinutes(202004189)). // maintenanceItem의 정수를 사용
                repairMan(repairMan).
                repairShop(repairShop).
                maintenanceItem(maintenanceItem).
                createdAt(LocalDateTime.now()).build();
    }
}
