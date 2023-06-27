package com.lucentblock.assignment2.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="repair_shop")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepairShop {

    @Id
    private Integer id;

    @Column
    private String name;

    @Column
    private String location;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;
}
