package com.lucentblock.assignment2.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="repair_shop")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RepairShop implements SoftDeletable{

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String location;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
