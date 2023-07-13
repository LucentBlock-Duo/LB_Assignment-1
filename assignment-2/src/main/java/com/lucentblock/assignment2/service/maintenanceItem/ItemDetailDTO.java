package com.lucentblock.assignment2.service.maintenanceItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemDetailDTO {
    private Long id;
    private Long maintenanceItemId;
    private Long repairManId;
    private Integer price;
}
