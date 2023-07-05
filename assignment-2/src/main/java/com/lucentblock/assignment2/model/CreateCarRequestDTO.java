package com.lucentblock.assignment2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateCarRequestDTO {
    @NotEmpty(message = "차량이름은 필수 항목입니다.")
    @JsonProperty(value = "car_name")
    private String carName;
    @JsonProperty(value = "user_email")
    private String userEmail;
    @JsonProperty(value = "car_manufacturer_id")
    private Long carManufacturerId;

    @JsonProperty(value = "bought_at")
    @NotNull(message = "차량 구입 시기는 필수 항목입니다.")
    private LocalDateTime boughtAt;
}
