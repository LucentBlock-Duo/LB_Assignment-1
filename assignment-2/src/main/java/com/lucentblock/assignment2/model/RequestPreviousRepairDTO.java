package com.lucentblock.assignment2.model;

import java.time.LocalDateTime;

public interface RequestPreviousRepairDTO {

    Long getUser_id();
    Long getCar_id(); // 차
    Long getRepair_shop_id(); // 정비소
    Long getRepair_man_id(); // 정비공
    Long getItem_detail_id(); // 정비항목
}
