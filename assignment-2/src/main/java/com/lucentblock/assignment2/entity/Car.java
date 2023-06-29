package com.lucentblock.assignment2.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "car")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Car {

    @Id
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="car_manufacturer_id")
    private CarManufacturer carManufacturerId;

    @Column(name="bought_at")
    private LocalDateTime boughtAt;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
