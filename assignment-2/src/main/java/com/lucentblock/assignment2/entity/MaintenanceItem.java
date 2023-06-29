package com.lucentblock.assignment2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "maintenance_item")
@ToString
public class MaintenanceItem {

    @Id
    private Long id;

    @Column(name = "name")
    private String itemName;

    @Column(name = "required_license")
    private int requiredLicense;

    @Column(name = "required_time")
    private int requiredTime;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
