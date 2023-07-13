package com.lucentblock.assignment2.entity.maintenanceItem;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.service.maintenanceItem.ItemDetailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Table(name = "item_details")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maintenance_item_id")
    private MaintenanceItem maintenanceItem;

    @ManyToOne
    @JoinColumn(name = "repair_man_id")
    private RepairMan repairMan;

    @Column(name = "price")
    private Integer price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static ItemDetailDTO toDTO(ItemDetail itemDetail) {
        return ItemDetailDTO.builder()
                .id(itemDetail.getId())
                .maintenanceItemId(itemDetail.getMaintenanceItem().getId())
                .repairManId(itemDetail.getRepairMan().getId())
                .price(itemDetail.getPrice())
                .build();
    }
}
