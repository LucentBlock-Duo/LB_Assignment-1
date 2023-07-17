package com.lucentblock.assignment2.entity.preference;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.car.CarManufacturer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_man_brand_preference")
public class RepairMainBrandPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "repair_man_id")
    RepairMan repairMan;

    @ManyToOne
    @JoinColumn(name = "car_manufacturer_id")
    CarManufacturer carManufacturer;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;
}
