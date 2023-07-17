package com.lucentblock.assignment2.model;

import java.time.LocalDateTime;

public interface RequestReserveDTO {
    Long getCar_id(); // 차
    Long getRepair_shop_id(); // 정비소
    Long getItem_detail_id(); // 정비항목
    LocalDateTime getStart_time(); // 예약시작
}
