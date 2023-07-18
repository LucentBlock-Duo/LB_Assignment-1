package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CreateRequestReserveDTO {
    @NotNull(message = "차를 선택 해주세요.")
    Long car_id; // 차
    @NotNull(message = "정비소를 선택 해주세요.")
    Long repair_shop_id; // 정비소
    @NotNull(message = "정비항목을 선택 해주세요.")
    Long item_detail_id; // 정비항목
    @NotNull(message = "예약 일자를 입력해주세요.")
    LocalDate date;
    @NotNull(message = "예약 시작 시간을 입력해주세요.")
    LocalTime start_time; // 예약시작
}
