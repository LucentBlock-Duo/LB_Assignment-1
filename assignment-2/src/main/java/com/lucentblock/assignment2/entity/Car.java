package com.lucentblock.assignment2.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "car")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    private Integer id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="car_manufacture_id")
    private CarManufacture carManufactureId;

    @Column(name="bought_at")
    private LocalDateTime boughtAt;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

}
