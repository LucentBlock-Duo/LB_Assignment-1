package com.lucentblock.assignment2.entity;

import com.lucentblock.assignment2.model.RESERVE_ERROR;
import com.lucentblock.assignment2.model.ResponseCode;
import com.lucentblock.assignment2.model.ResponseReserveDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "reserve")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Reserve implements SoftDeletable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "repair_man_id")
    private RepairMan repairMan;

    @ManyToOne
    @JoinColumn(name = "repair_shop_id")
    private RepairShop repairShop;

    @ManyToOne
    @JoinColumn(name = "maintenance_item_id")
    private MaintenanceItem maintenanceItem;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    public ResponseReserveDTO toSuccessDto(){
        return ResponseReserveDTO.builder().car_name(car.getName()).
                                            repair_man_id(repairMan.getName()).
                                            repair_shop_name(repairShop.getName()).
                                            maintenance_item_name(maintenanceItem.getItemName()).
                                            start_time(startTime).
                                            end_time(endTime).
                                            responseCode(ResponseCode.data("success",0)).build();
    }

    public ResponseReserveDTO toFailureDto(int code){
        return ResponseReserveDTO.builder()
                .responseCode(ResponseCode.data(RESERVE_ERROR.find(code).msg(),code)).build();
    }
}
