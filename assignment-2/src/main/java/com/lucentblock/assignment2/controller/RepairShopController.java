package com.lucentblock.assignment2.controller;


import com.lucentblock.assignment2.model.GPSRequestDTO;
import com.lucentblock.assignment2.model.GPSResponseDTO;
import com.lucentblock.assignment2.model.RepairShopSearchRequestDTO;
import com.lucentblock.assignment2.service.repair_shop.RepairShopService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/repairShop")
public class RepairShopController {

    private final RepairShopService repairShopService;

    @GetMapping("/apis/repair_shop_search")
    private List<GPSResponseDTO> readByAroundLoc
            (@RequestParam Long userId,@RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude){

        GPSRequestDTO gpsRequestDTO = repairShopService.makeRequestDTO(userId,latitude, longitude,false);
        return repairShopService.searchByAroundRepairShop(gpsRequestDTO);
    } // Static searching : by selected GPS Info

    @GetMapping("/apis/repair_shop_proximate")
    private GPSResponseDTO readByCurrentLoc
            (@RequestParam Long userId, @RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude) throws Exception {

        GPSRequestDTO gpsRequestDTO = repairShopService.makeRequestDTO(userId, latitude, longitude,true);
        return repairShopService.searchProximateRepairShop(gpsRequestDTO);
    } // Around searching : by User's GPS Info

    @PostMapping("/apis/kjj1299fsdhPZeCsnyroJ2jKaA100g2")
    private List<GPSResponseDTO> createByExistData(@RequestParam String keyword) throws IOException, URISyntaxException, ParseException, InterruptedException {
        return repairShopService.makeAuto(keyword);
    } // Build Repair_shop data from exist data

    @PostMapping("/apis/kjj1299fsdhPZeCsnyroJ2jKaA100g2make")
    private GPSResponseDTO createByDeveloper(@RequestParam String address, @RequestParam String name) throws IOException, URISyntaxException, ParseException, InterruptedException {
        return repairShopService.makeManual(address, name);
    } // Build Repair_shop data By developer


}
