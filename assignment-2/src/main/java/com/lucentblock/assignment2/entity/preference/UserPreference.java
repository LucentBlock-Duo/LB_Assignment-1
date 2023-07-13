package com.lucentblock.assignment2.entity.preference;

import com.lucentblock.assignment2.entity.CarManufacturer;
import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import com.lucentblock.assignment2.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "user_preference")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_manufacturer_id")
    private CarManufacturer carManufacturer;

    @ManyToOne
    @JoinColumn(name = "maintenance_item_id")
    private MaintenanceItem maintenanceItem;
}

