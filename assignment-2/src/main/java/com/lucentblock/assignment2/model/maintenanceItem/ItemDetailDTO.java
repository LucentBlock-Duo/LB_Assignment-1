package com.lucentblock.assignment2.model.maintenanceItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemDetailDTO {
    private Long itemDetailId;
    private String itemName;
    private Integer requiredLicense;
    private Integer requiredTime;
    private Integer price;
}
