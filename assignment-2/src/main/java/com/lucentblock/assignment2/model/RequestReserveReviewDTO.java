package com.lucentblock.assignment2.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RequestReserveReviewDTO {
    Long reserve_id; // 예약
    Long repair_man_id; // 정비공
    Double value; // 평점
}
