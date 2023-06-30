package com.lucentblock.assignment2.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ResponseReserveDTO {
    String car_name; // 차
    String repair_shop_name; // 정비소
    String repair_man_id; // 정비공
    String maintenance_item_name; // 정비항목
    LocalDateTime start_time; // 시작 시간
    LocalDateTime end_time; // 종료 시간

    ResponseCode responseCode;
}
