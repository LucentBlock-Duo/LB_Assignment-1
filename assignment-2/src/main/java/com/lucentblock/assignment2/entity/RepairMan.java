package com.lucentblock.assignment2.entity;


import com.lucentblock.assignment2.model.RepairManInfoDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_man")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RepairMan implements SoftDeletable {

    @Id
    private Long id;

    @Column
    private String name;

    @Column(name = "license_id")
    private Integer licenseId;

    @Column(name = "career_start_at")
    private LocalDateTime careerStartAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "evaluated_num")
    private Integer evaluatedNum;

    @Column(name = "evaluation_grade")
    private Double evaluationGrade;


    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setEvaluatedNum(Integer evaluatedNum) {
        this.evaluatedNum = evaluatedNum;
    }

    public void setEvaluationGrade(Double evaluationGrade) {
        this.evaluationGrade = evaluationGrade;
    }
    public static RepairManInfoDTO toDTO(RepairMan repairMan) {
        return RepairManInfoDTO.builder()
                .id(repairMan.getId())
                .name(repairMan.getName())
                .licenseId(repairMan.getLicenseId())
                .careerStartAt(repairMan.getCareerStartAt())
                .build();
    }
}
