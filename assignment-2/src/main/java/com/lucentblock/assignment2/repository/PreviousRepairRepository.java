package com.lucentblock.assignment2.repository;


import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.model.ForeignKeySetForPreviousRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PreviousRepairRepository extends JpaRepository<PreviousRepair,Long> {

    List<PreviousRepair> findAllByUserAndDeletedAtIsNotNull(User user);
    List<PreviousRepair> findAllByRepairManAndDeletedAtIsNotNull(RepairMan user);

    List<PreviousRepair> findAllByUserAndRepairManAndDeletedAtIsNotNull(User user, RepairMan repairMan);

    default List<PreviousRepair> findPreviousRepair(User user,RepairMan repairMan){
        if(user==null && repairMan==null){
            throw new RuntimeException("No Data Input");
        }else{
            if(repairMan==null){
                return findAllByUserAndDeletedAtIsNotNull(user);
            }else if(user==null){
                return findAllByRepairManAndDeletedAtIsNotNull(repairMan);
            }else{
                return findAllByUserAndRepairManAndDeletedAtIsNotNull(user,repairMan);
            }
        }
    }

    default List<PreviousRepair> findPreviousRepairDetail(ForeignKeySetForPreviousRepair set,
                                                          LocalDate start, LocalDate end){
        List<PreviousRepair> result=findPreviousRepair(set.getUser(),set.getRepairMan());

        return  result.stream()
                .filter(repair-> set.getCar() == null || repair.getCar().equals(set.getCar()))
                .filter(repair-> set.getRepairShop() == null || repair.getRepairShop().equals(set.getRepairShop()))
                .filter(repair-> set.getRepairMan() == null || repair.getRepairMan().equals(set.getRepairMan()))
                .filter(repair-> set.getMaintenanceItem() == null || repair.getMaintenanceItem().equals(set.getMaintenanceItem()))
                .filter(repair-> start == null ||
                        repair.getStartTime().toLocalDate().isAfter(start) ||
                        repair.getStartTime().toLocalDate().isEqual(start))
                .filter(repair -> end == null ||
                        repair.getStartTime().toLocalDate().isBefore(end) ||
                        repair.getStartTime().toLocalDate().isEqual(end)).toList();
    }

}
