package com.lucentblock.assignment2.entity.item;

import com.lucentblock.assignment2.entity.SoftDeletable;
import com.lucentblock.assignment2.model.maintenanceItem.MaintenanceItemDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "maintenance_item")
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceItem implements SoftDeletable {

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

    public static MaintenanceItemDTO toDTO(MaintenanceItem maintenanceItem) {
        return MaintenanceItemDTO.builder()
                .id(maintenanceItem.getId())
                .itemName(maintenanceItem.getItemName())
                .requiredLicense(maintenanceItem.getRequiredLicense())
                .requiredTime(maintenanceItem.getRequiredTime())
                .build();
    }
}
