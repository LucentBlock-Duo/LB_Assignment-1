package com.lucentblock.assignment2.service.item;

import com.lucentblock.assignment2.model.RepairManInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemDetailDTO {
    private Long id;
    private Long maintenanceItemId;
    private RepairManInfoDTO repairManInfoDTO;
    private Integer price;
}
