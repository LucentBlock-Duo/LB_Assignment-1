package com.lucentblock.assignment2.controller;


import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.RepairStatus;
import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.model.*;
import com.lucentblock.assignment2.service.ReserveService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
@Slf4j
public class ReserveController {

    private final ReserveService reserveService;

    public ReserveController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @PostMapping
    private List<ResponseReserveDTO> create(@Valid @RequestBody List<CreateRequestReserveDTO> dto){
        return reserveService.createReserve(dto);
    }

    @GetMapping
    private List<ResponseReserveDTO> read(@RequestParam Long carId){
        return reserveService.findReserveByCarId(carId).stream().map(Reserve::toDto).toList();
    }

    @PutMapping
    private ResponseReserveDTO update(@RequestBody UpdateRequestReserveDTO dto){
        return reserveService.updateReserve(dto);
    }

    @DeleteMapping
    private Reserve delete(@RequestBody Long reserveId){
        return reserveService.deleteReserve(reserveId);
    }


    @PostMapping("/status")
    private ResponseReserveDTO updateStatus(@RequestBody Long reserveId, @RequestBody Integer status){
        return reserveService.setStatus(reserveId,status);
    }

    @PostMapping("/review")
    private RepairMan review(@RequestBody RequestReserveReviewDTO dto){
        return reserveService.evaluate(dto);
    }
}
