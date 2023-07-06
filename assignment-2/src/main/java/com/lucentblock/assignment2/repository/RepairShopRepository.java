package com.lucentblock.assignment2.repository;


import com.lucentblock.assignment2.entity.CountryLocation;
import com.lucentblock.assignment2.entity.RepairShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairShopRepository extends JpaRepository<RepairShop,Long> {

    List<RepairShop> findAllByLocationAndNameContainingAndDeletedAtIsNull(CountryLocation location, String name);

    default List<RepairShop> findByLocationAndKeyword(CountryLocation location,String keyword) {
        return findAllByLocationAndNameContainingAndDeletedAtIsNull(location,keyword);
    }
}
