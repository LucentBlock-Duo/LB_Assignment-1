package com.lucentblock.assignment2.entity;


import com.lucentblock.assignment2.model.GPSResponseDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="repair_shop")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RepairShop implements SoftDeletable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name; // 블루핸즈봉명점

    @Column
    private String province; // 시도

    @Column
    private String city; // 시군구

    private String address; // 상세주소

    @Column(name="road_address")
    private String roadAddress; // 길

    private BigDecimal latitude; // 위도

    private BigDecimal longitude; // 경도

    @Column(name="post_num")
    private Integer postNum; // 우편번호

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public GPSResponseDTO toDto(){
        return GPSResponseDTO.builder()
                .province(province)
                .city(city)
                .latitude(latitude)
                .longitude(longitude)
                .postNum(postNum)
                .address(address)
                .name(name).build();
    }
}
