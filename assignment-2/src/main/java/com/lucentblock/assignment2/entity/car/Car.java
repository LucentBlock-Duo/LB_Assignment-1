package com.lucentblock.assignment2.entity.car;


import com.lucentblock.assignment2.entity.SoftDeletable;
import com.lucentblock.assignment2.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "car")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Car implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "license_plate_no")
    private String licensePlateNo;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="car_manufacturer_id")
    private CarManufacturer carManufacturer;

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
