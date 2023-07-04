package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.*;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "예약 아이디가 존재하지 않습니다.")
    Long reserve_id; // 예약 아이디

    @NotEmpty(message = "차를 선택 해주세요.")
    Long car_id; // 차

    @NotEmpty(message = "정비소를 선택 해주세요.")
    Long repair_shop_id; // 정비소

    @NotEmpty(message = "정비공을 선택 해주세요.")
    Long repair_man_id; // 정비공

    @NotEmpty(message = "정비항목을 선택 해주세요.")
    Long maintenance_item_id; // 정비항목

    @NotEmpty(message = "예약 시작 시간을 입력해주세요.")
    LocalDateTime start_time; // 예약시작

    public boolean isValid(){
        return reserve_id!=null && car_id!=null && repair_shop_id!=null && repair_man_id!=null &&
                maintenance_item_id !=null && start_time!=null;
    }
}
