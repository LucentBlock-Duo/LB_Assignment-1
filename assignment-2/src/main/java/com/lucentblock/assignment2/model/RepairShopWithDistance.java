package com.lucentblock.assignment2.model;

import java.math.BigDecimal;

public interface RepairShopWithDistance {
    Long getId();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
    BigDecimal getDistance();
    String getProvince(); // 시도
    String getCity(); // 시군구
    String getAddress(); // 상세주소

    String getName();
    Integer getPost_num(); // 우편번호

    default GPSResponseDTO toDto(){
        return GPSResponseDTO.builder()
                .name(getName())
                .province(getProvince())
                .city(getCity())
                .longitude(getLongitude())
                .latitude(getLatitude())
                .postNum(getPost_num())
                .distance(getDistance())
                .address(getAddress()).build();

    }
}
