package com.lucentblock.assignment2.model;


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
public class UpdateRequestPreviousRepairDTO implements RequestPreviousRepairDTO {
    @NotNull(message = "예약 아이디가 존재하지 않습니다.")
    Long repair_id; // 예약 아이디

    @NotNull(message = "유저가 존재하지 않습니다.")
    Long user_id; // 유저 아이디

    @NotNull(message = "차를 선택 해주세요.")
    Long car_id; // 차

    @NotNull(message = "정비소를 선택 해주세요.")
    Long repair_shop_id; // 정비소

    @NotNull(message = "정비공을 선택 해주세요.")
    Long repair_man_id; // 정비공

    @NotNull(message = "정비항목을 선택 해주세요.")
    Long item_detail_id; // 정비항목

    @NotNull(message = "정비 시작 시간을 입력해주세요.")
    LocalDateTime start_time; // 예약시작

    @NotNull(message = "정비 종료 시간을 입력해주세요.")
    LocalDateTime end_time; // 예약시작
}
