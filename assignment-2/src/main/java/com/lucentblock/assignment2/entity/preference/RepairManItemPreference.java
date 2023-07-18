package com.lucentblock.assignment2.entity.preference;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_man_item_preference")
public class RepairManItemPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "repair_man_id")
    RepairMan repairMan;

    @ManyToOne
    @JoinColumn(name = "maintenance_item_id")
    MaintenanceItem maintenanceItem;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;
}
