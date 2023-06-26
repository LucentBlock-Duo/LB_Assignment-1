package com.lucentblock.assignment2.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_description")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDescription {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="car_id")
    private Car carId;

    @Column
    private String color;

    @Column
    private String seats;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

}
