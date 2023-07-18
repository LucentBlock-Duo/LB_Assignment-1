package com.lucentblock.assignment2.entity;

import com.lucentblock.assignment2.model.ResponsePreviousRepairDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter
@Entity
@Table(name = "previous_repair")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PreviousRepair implements SoftDeletable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "repair_man_id")
    private RepairMan repairMan;

    @ManyToOne
    @JoinColumn(name = "repair_shop_id")
    private RepairShop repairShop;

    @ManyToOne
    @JoinColumn(name = "maintenance_item_id")
    private MaintenanceItem maintenanceItem;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    private Integer status;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public ResponsePreviousRepairDTO toDto(){
        return ResponsePreviousRepairDTO.builder()
                .car_name(car.getName())
                .user_email(user.getEmail())
                .repair_man_name(repairMan.getName())
                .repair_shop_name(repairShop.getName())
                .maintenance_item_name(maintenanceItem.getItemName())
                .startTime(getStartTime())
                .endTime(getEndTime())
                .status(RepairStatus.status(getStatus()))
                .build();
    }

}