package com.lucentblock.assignment2.model;

import com.lucentblock.assignment2.model.maintenanceItem.ItemDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class RepairManInfoDTO {
    private Long id;
    private String name;
    private Integer licenseId;
    private LocalDateTime careerStartAt;
    private List<ItemDetailDTO> availableItems;
}
