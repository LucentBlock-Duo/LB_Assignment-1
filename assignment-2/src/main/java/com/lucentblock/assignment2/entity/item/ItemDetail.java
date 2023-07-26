package com.lucentblock.assignment2.entity.item;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.model.maintenanceItem.ItemDetailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Table(name = "item_detail")
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
                .itemDetailId(itemDetail.getId())
                .maintenanceItemId(itemDetail.getMaintenanceItem().getId())
                .itemName(itemDetail.getMaintenanceItem().getItemName())
                .requiredLicense(itemDetail.getMaintenanceItem().getRequiredLicense())
                .requiredTime(itemDetail.getMaintenanceItem().getRequiredTime())
                .price(itemDetail.getPrice())
                .build();
    }
}
