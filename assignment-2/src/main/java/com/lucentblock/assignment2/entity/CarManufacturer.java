package com.lucentblock.assignment2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "car_manufacturer")
public class CarManufacturer {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
