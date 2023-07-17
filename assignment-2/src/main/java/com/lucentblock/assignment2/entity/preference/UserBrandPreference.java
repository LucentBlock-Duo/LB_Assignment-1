package com.lucentblock.assignment2.entity.preference;

import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.car.CarManufacturer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_brand_preference")
public class UserBrandPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "car_manufacturer_id")
    CarManufacturer carManufacturer;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;
}
