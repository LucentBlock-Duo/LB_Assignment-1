package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.*;
import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.exception.PreviousRepairNotFoundException;
import com.lucentblock.assignment2.model.*;
import com.lucentblock.assignment2.repository.PreviousRepairRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import static com.lucentblock.assignment2.model.PreviousRepairSearchRequestDTO.*;

@Service
@RequiredArgsConstructor
public class PreviousRepairService {

    private final PreviousRepairRepository previousRepairRepository;
    private final RepairManService repairManService;
    private final UserService userService;
    private final EntityManager em;



    public PreviousRepair createPreviousRepair(Long reserveId) {
        Reserve reserve = em.find(Reserve.class, reserveId);
        PreviousRepair previousRepair = reserve.toPreviousRepairEntity();

        return previousRepairRepository.save(previousRepair);
    }

    public List<ResponsePreviousRepairDTO> userSearch(int mode,String param){
        List<PreviousRepair> result=new ArrayList<>();
        List<User> userList=switch (mode){ //
            case 0 -> userService.getUsersByEmail(param);// email
            case 1 -> userService.getUsersByUsername(param);// name
            default -> null;
        };

        assert userList != null;

        userList.forEach(user ->
                result.addAll(previousRepairRepository.findAllByUserAndDeletedAtIsNotNull(user)));

        return result.stream().map(PreviousRepair::toDto).toList();
    } //

    public List<ResponsePreviousRepairDTO> repairManSearch(String name){
        List<PreviousRepair> result=new ArrayList<>();

        repairManService.getRepairMenByName(name).
                forEach(repairMan ->
                        result.addAll(previousRepairRepository.findAllByRepairManAndDeletedAtIsNotNull(repairMan)));

        return result.stream().map(PreviousRepair::toDto).toList();
    }

    public List<ResponsePreviousRepairDTO> commonSearch(Common dto){
        User user=em.find(User.class,dto.getUser_id());
        RepairMan repairMan=em.find(RepairMan.class,dto.getRepair_man_id());

        return previousRepairRepository.findPreviousRepair(user,repairMan).stream()
                .map(PreviousRepair::toDto).toList();
    }

    public List<ResponsePreviousRepairDTO> detailSearch(Detail dto) {
        ForeignKeySetForPreviousRepair set=getForeignKeySet(dto);

        return previousRepairRepository.findPreviousRepairDetail(set,dto.getStartTime(),dto.getEndTime()).stream()
                .map(PreviousRepair::toDto).toList();
    }

    private ForeignKeySetForPreviousRepair getForeignKeySet(RequestPreviousRepairDTO dto) {
        User user = em.find(User.class, dto.getUser_id());
        Car car = em.find(Car.class, dto.getCar_id());
        RepairMan repairMan = em.find(RepairMan.class, dto.getRepair_man_id());
        RepairShop repairShop = em.find(RepairShop.class, dto.getRepair_shop_id());
        MaintenanceItem maintenanceItem = em.find(ItemDetail.class, dto.getItem_detail_id()).getMaintenanceItem();

        return ForeignKeySetForPreviousRepair.builder()
                .user(user)
                .car(car)
                .repairMan(repairMan)
                .repairShop(repairShop)
                .maintenanceItem(maintenanceItem).build();
    }


    @Transactional
    public PreviousRepair deleteRepair(Long repairId){
        PreviousRepair repair = previousRepairRepository.findById(repairId)
                .orElseThrow(()->new PreviousRepairNotFoundException("기록을 찾을 수 없습니다."));

        repair.delete();
        previousRepairRepository.save(repair);

        return repair;
    }
}
