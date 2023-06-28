package com.lucentblock.assignment2.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_description")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarDescription {

    @Id
    private Long id;

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

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
