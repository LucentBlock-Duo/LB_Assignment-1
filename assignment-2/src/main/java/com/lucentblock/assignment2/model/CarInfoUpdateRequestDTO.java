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
public class CarInfoUpdateRequestDTO {
    @JsonProperty(value = "license_plate_no")
    @NotNull(message = "차량번호는 필수 항목입니다.")
    private String licensePlateNo;

    @JsonProperty(value = "car_name")
    @NotEmpty(message = "차량 이름은 필수 항목입니다.")
    private String carName;

    @JsonProperty(value = "car_manufacturer_id")
    @NotNull(message = "제조사명은 필수 항목입니다.")
    private Long carManufacturerId;

    @JsonProperty(value = "bought_at")
    @NotNull(message = "구입일자는 필수 항목입니다.")
    private LocalDateTime boughtAt;
}
