package com.lucentblock.assignment2.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@ToString
public class GPSRequestDTO {
    private String province; // 시도
    private String city; // 시군구
    private BigDecimal latitude; // 위도
    private BigDecimal longitude; // 경도
}
