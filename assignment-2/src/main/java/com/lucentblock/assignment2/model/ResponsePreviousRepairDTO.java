package com.lucentblock.assignment2.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ResponsePreviousRepairDTO {
    Long id;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String user_email;
    String car_name;
    String repair_man_name;
    String repair_shop_name;
    String maintenance_item_name;
    String status;
}
