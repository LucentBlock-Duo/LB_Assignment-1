package com.lucentblock.assignment2.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class PreviousRepairSearchRequestDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Common{
        String user_id;
        String repair_man_id;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail implements RequestPreviousRepairDTO{
        Long id;
        LocalDate startTime;
        LocalDate endTime;
        Long user_id;
        Long car_id;
        Long repair_man_id;
        Long repair_shop_id;
        Long item_detail_id;
    }
}