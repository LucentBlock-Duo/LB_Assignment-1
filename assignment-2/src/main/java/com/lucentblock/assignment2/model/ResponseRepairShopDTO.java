package com.lucentblock.assignment2.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ResponseRepairShopDTO {
    Long id;
    String name; // 블루핸즈봉명점
    String province; // 시도
    String city; // 시군구
    String address; // 상세주소
    String roadAddress; // 길
    BigDecimal latitude; // 위도
    BigDecimal longitude; // 경도
    Integer postNum; // 우편번호
}
