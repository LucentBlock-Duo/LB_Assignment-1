package com.lucentblock.assignment2.entity;

import com.lucentblock.assignment2.model.ResponseCarManufacturerDTO;
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
@Table(name = "car_manufacturer")
@ToString
public class CarManufacturer implements SoftDeletable{

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public ResponseCarManufacturerDTO toDto(){
        return ResponseCarManufacturerDTO.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
