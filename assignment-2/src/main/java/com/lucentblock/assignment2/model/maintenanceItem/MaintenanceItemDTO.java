package com.lucentblock.assignment2.model.maintenanceItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MaintenanceItemDTO {
    private Long id;
    private String itemName;
    private int requiredLicense;
    private int requiredTime;
}
