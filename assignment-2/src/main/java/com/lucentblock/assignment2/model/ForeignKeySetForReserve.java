//package com.lucentblock.assignment2.model;
//
//
//import com.lucentblock.assignment2.entity.car.Car;
//import com.lucentblock.assignment2.entity.item.ItemDetail;
//import com.lucentblock.assignment2.entity.RepairMan;
//import com.lucentblock.assignment2.entity.RepairShop;
//import com.lucentblock.assignment2.exception.ReserveErrorCode;
//import com.lucentblock.assignment2.exception.ReservedWithNoMatchValueException;
//import lombok.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter @Setter
//@Builder
//@AllArgsConstructor
//@ToString
//public class ForeignKeySetForReserve {
//    Car car;
//    RepairMan repairMan;
//    RepairShop repairShop;
//    ItemDetail itemDetail;
//
//    public boolean isValidate(){
//        if(car==null || repairMan==null || repairShop==null || itemDetail==null){
//            List<String> list=new ArrayList<>();
//
//            if(car==null) list.add("car");
//            if(repairMan==null) list.add("repair_shop");
//            if(repairShop==null) list.add("repair_man");
//            if(itemDetail==null) list.add("item_detail");
//
//            throw new ReservedWithNoMatchValueException(ReserveErrorCode.ERROR_103,list);
//        }
//
//        return true;
//    }
//}
