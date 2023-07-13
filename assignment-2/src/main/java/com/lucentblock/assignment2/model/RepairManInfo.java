package com.lucentblock.assignment2.model;

import com.lucentblock.assignment2.entity.RepairMan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class RepairManInfo {
    private Long id;
    private String name;
    private Integer licenseId;
    private LocalDateTime careerStartAt;

    public static RepairManInfo toDTO(RepairMan repairMan) {
        return RepairManInfo.builder()
                .id(repairMan.getId())
                .name(repairMan.getName())
                .licenseId(repairMan.getLicenseId())
                .careerStartAt(repairMan.getCareerStartAt())
                .build();
    }
}
