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
public class CreateRequestReserveDTO implements RequestReserveDTO {
    Long car_id; // 차
    Long repair_shop_id; // 정비소
    Long repair_man_id; // 정비공
    Long maintenance_item_id; // 정비항목
    LocalDateTime start_time; // 예약시작

    public boolean isValidate(){
        return car_id!=null && repair_shop_id!=null && repair_man_id!=null && maintenance_item_id!=null &&
                start_time!=null;
    }
    public Reserve toEntity(ForeignKeySetForReserve set){
        return Reserve.builder().
                car(set.getCar()).
                startTime(start_time).
                endTime(start_time.plusMinutes(set.getMaintenanceItem().getRequiredTime())). // maintenanceItem의 정수를 사용
                repairMan(set.getRepairMan()).
                repairShop(set.getRepairShop()).
                maintenanceItem(set.getMaintenanceItem()).
                createdAt(LocalDateTime.now()).build();
    }
}
