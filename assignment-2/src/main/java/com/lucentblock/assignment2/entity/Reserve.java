package com.lucentblock.assignment2.entity;

import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.model.ResponseReserveDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "date")
    private LocalDate date;

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
    @JoinColumn(name = "item_detail_id")
    private ItemDetail itemDetail;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name="status") // 상태 추가 default : 0
    @ColumnDefault("0")
    private Integer status;

    @Column(name="is_reviewed") // 리뷰 여부 default : false
    @ColumnDefault("false")
    private Boolean isReviewed;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    public ResponseReserveDTO toDto(){
        return ResponseReserveDTO.builder().car_name(car.getName()).
                                            repair_man_id(repairMan.getName()).
                                            repair_shop_name(repairShop.getName()).
                                            date(date).
                                            maintenance_item_name(itemDetail.getMaintenanceItem().getItemName()).
                                            start_time(startTime).
                                            end_time(endTime).
                                            status(RepairStatus.NOT_STARTED.status()).build();
    }

    public PreviousRepair toPreviousRepairEntity(){
        return PreviousRepair.builder()
                .repairDate(getDate())
                .startTime(getStartTime())
                .endTime(getEndTime())
                .user(getCar().getUser())
                .car(getCar())
                .repairMan(getRepairMan())
                .repairShop(getRepairShop())
                .itemDetail(getItemDetail())
                .status(getStatus())
                .build();
    }
}
