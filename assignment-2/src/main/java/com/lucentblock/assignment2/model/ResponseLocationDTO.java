package com.lucentblock.assignment2.model;


import com.lucentblock.assignment2.entity.CountryLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseLocationDTO {
    Long id;
    String province;
    String city;
}
