package com.lucentblock.assignment2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RepairmanAvailabilityQueryDTO {
    @NotNull(message = "date 는 필수 항목입니다.")
    @JsonProperty(value = "date")
    private LocalDate date;
    @NotNull(message = "start_time 은 필수 항목입니다.")
    @JsonProperty(value = "start_time")
    private LocalTime startTime;
    @NotNull
    @JsonProperty(value = "maintenance_item_id")
    private Long maintenanceItemId;
}
