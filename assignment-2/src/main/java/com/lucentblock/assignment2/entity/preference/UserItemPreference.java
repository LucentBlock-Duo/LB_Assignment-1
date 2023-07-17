package com.lucentblock.assignment2.entity.preference;

import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_item_preference")
public class UserItemPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "maintenance_item_id")
    MaintenanceItem maintenanceItem;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;
}
