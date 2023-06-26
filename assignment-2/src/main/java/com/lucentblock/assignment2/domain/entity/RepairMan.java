package com.lucentblock.assignment2.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_man")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepairMan {

    @Id
    private Integer id;

    @Column
    private String name;

    @Column(name="license_id")
    private String licenseId;

    @Column(name="career_start_at")
    private LocalDateTime careerStartAt;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

}
