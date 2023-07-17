package com.lucentblock.assignment2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemDetailQueryDTO {
    @NotNull(message = "repair_man_id 는 필수 항목입니다.")
    @JsonProperty(value = "repair_man_id")
    private Long repairManId;

    @NotNull(message = "maintenance_item_id 는 필수 항목입니다.")
    @JsonProperty(value = "maintenance_item_id")
    private Long maintenanceItemId;
}
