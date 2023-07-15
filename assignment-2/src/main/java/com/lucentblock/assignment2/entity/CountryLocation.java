package com.lucentblock.assignment2.entity;


import com.lucentblock.assignment2.model.ResponseLocationDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Table(name="location")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class CountryLocation implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String province; // 시도

    @Column
    private String city; // 시군구

    @Column(name="address")
    private String address; // 상세주소

    @Column(name="latitude")
    private Double latitude; // 위도

    @Column(name="longitude")
    private Double longitude; // 경도

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public ResponseLocationDTO toDto(){
        return ResponseLocationDTO.builder()
                .id(id)
                .province(province)
                .city(city).build();
    }
}
