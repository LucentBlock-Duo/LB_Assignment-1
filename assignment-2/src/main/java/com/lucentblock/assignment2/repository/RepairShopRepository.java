package com.lucentblock.assignment2.repository;


import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.model.RepairShopWithDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RepairShopRepository extends JpaRepository<RepairShop,Long> {

    List<RepairShop> findAllByProvinceAndCityAndNameContainingAndDeletedAtIsNull(String province, String city, String name);

    @Query( value = "SELECT r.id, r.latitude, r.longitude, r.province, r.city, r.address, r.name, r.post_num, " +
            "(ST_DISTANCE_SPHERE(POINT(r.longitude, r.latitude),POINT(?2, ?1))) as distance " +
            "FROM repair_shop r " +
            "HAVING distance<=?3 ",nativeQuery = true)
    List<RepairShopWithDistance> findRepairShopInDistanceRangeWithoutCity
            (BigDecimal givenLatitude, BigDecimal givenLongitude, BigDecimal distance);

    @Query( value = "SELECT r.id, r.latitude, r.longitude, r.province, r.city, r.address, r.name, r.postNum, " +
            "ST_DISTANCE_SPHERE(POINT(r.longitude, r.latitude),POINT(?2, ?1)) as distance " +
            "FROM repair_shop r " +
            "HAVING r.city=?4 " +
            "AND distance<=?3 ",nativeQuery = true)
    List<RepairShopWithDistance> findRepairShopInDistanceRange
            (BigDecimal givenLatitude, BigDecimal givenLongitude, BigDecimal distance, String city);


    default List<RepairShop> findByLocationAndKeyword(String province, String city,String keyword) {
        return findAllByProvinceAndCityAndNameContainingAndDeletedAtIsNull(province,city,keyword);
    }
}
