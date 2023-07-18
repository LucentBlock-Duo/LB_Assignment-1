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
public class GPSResponseDTO {
    private String name;
    private String province; // 시도
    private String city; // 시군구
    private String address; // 상세주소
    private BigDecimal latitude; // 위도
    private BigDecimal longitude; // 경도
    private BigDecimal distance;
    private Integer postNum; // 우편번호
}
