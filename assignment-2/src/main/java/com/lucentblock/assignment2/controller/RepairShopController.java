package com.lucentblock.assignment2.controller;


import com.lucentblock.assignment2.model.RepairShopSearchRequestDTO;
import com.lucentblock.assignment2.model.ResponseRepairShopDTO;
import com.lucentblock.assignment2.service.RepairShopService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/repairShop")
public class RepairShopController {

    private final RepairShopService repairShopService;

    @GetMapping("/api/repairShop")
    private List<ResponseRepairShopDTO> read(@RequestParam String keyword,Long location_id){
        return repairShopService.searchResult(new RepairShopSearchRequestDTO(keyword,location_id));
    }

    @PostMapping("/api/kjj1299fsdhPZeCsnyroJ2jKaA100g2")
    private boolean databuild() throws IOException, URISyntaxException, ParseException, InterruptedException {
        return repairShopService.makeLocationDataV2();
    }
}
