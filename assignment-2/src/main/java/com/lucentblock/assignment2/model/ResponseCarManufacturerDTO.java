package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.Reserve;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
public class ResponseCarManufacturerDTO {
    Long id;
    String name;
}
