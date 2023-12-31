package com.lucentblock.assignment2.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ResponseReserveDTO {
    String car_name; // 차
    String repair_shop_name; // 정비소
    String repair_man_id; // 정비공
    String maintenance_item_name; // 정비항목
    LocalTime start_time; // 시작 시간
    LocalTime end_time; // 종료 시간
    LocalDate date;
    String status;
}
