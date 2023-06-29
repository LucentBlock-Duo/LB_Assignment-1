package com.lucentblock.assignment2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "maintenance_item")
public class MaintenanceItem {

    @Id
    private long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "required_license")
    private int requiredLicense;

    @Column(name = "required_time")
    private int requiredTime;
}
