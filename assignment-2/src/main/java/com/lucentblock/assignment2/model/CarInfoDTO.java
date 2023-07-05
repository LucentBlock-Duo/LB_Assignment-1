package com.lucentblock.assignment2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucentblock.assignment2.entity.Car;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CarInfoDTO {

    @JsonProperty(value = "user_email")
    @NotEmpty(message = "이메일은 필수 항목입니다.")
    private String userEmail;

    @JsonProperty(value = "user_name")
    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String userName;

    @JsonProperty(value = "car_id")
    @NotNull(message = "carId 는 필수 항목입니다.")
    private Long carId;

    @JsonProperty(value = "car_name")
    @NotEmpty(message = "차량 이름은 필수 항목입니다.")
    private String carName;

    @JsonProperty(value = "car_manufacturer_name")
    @NotEmpty(message = "제조사명은 필수 항목입니다.")
    private String carManufacturerName;

    @JsonProperty(value = "bought_at")
    @NotNull(message = "구입일자는 필수 항목입니다.")
    private LocalDateTime boughtAt;

    public static CarInfoDTO carToCarInfoDTO(Car car) {
        return CarInfoDTO.builder()
                .carId(car.getId())
                .userEmail(car.getUser().getEmail())
                .userName(car.getUser().getName())
                .carName(car.getName())
                .carManufacturerName(car.getCarManufacturer().getName())
                .boughtAt(car.getBoughtAt())
                .build();
    }
}
