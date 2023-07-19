package com.lucentblock.assignment2.controller;


import com.lucentblock.assignment2.entity.PreviousRepair;
import com.lucentblock.assignment2.model.ResponsePreviousRepairDTO;
import com.lucentblock.assignment2.service.PreviousRepairService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import static com.lucentblock.assignment2.model.PreviousRepairSearchRequestDTO.*;

import java.util.List;

@RestController
@RequestMapping("/api/previous_reserve")
@Slf4j
@RequiredArgsConstructor
public class PreviousRepairController {

    private final PreviousRepairService previousRepairService;


    @PostMapping
    private PreviousRepair create(@RequestBody Long reserve_id){
        return previousRepairService.createPreviousRepair(reserve_id);
    }

    @GetMapping("/common_search")
    private List<ResponsePreviousRepairDTO> readCommon(@RequestBody Common dto){
        return previousRepairService.commonSearch(dto);
    }

    @GetMapping("/detail_search")
    private List<ResponsePreviousRepairDTO> readDetail(@RequestBody Detail dto){
        return previousRepairService.detailSearch(dto);
    }

    @DeleteMapping
    private PreviousRepair delete(@RequestBody Long repairId){
        return previousRepairService.deleteRepair(repairId);
    }
}
