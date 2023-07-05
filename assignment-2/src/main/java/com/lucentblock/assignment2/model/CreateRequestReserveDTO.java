package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "차를 선택 해주세요.")
    Long car_id; // 차

    @NotNull(message = "정비소를 선택 해주세요.")
    Long repair_shop_id; // 정비소

    @NotNull(message = "정비공을 선택 해주세요.")
    Long repair_man_id; // 정비공

    @NotNull(message = "정비항목을 선택 해주세요.")
    Long maintenance_item_id; // 정비항목

    @NotNull(message = "예약 시작 시간을 입력해주세요.")
    LocalDateTime start_time; // 예약시작

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
