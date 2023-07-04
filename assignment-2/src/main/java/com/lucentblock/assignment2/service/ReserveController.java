package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.model.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class ReserveController {

    private final ReserveService reserveService;

    public ReserveController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @PostMapping("/api/reserve/create")
    private ResponseReserveDTO create(@Valid @RequestBody CreateRequestReserveDTO dto){
        return reserveService.createReserve(dto);
    }

    @GetMapping("/api/reserve/read") // 검증완료
    private List<ResponseReserveDTO> read(@RequestParam Long carId){
        return reserveService.findReserveByCarId(carId).stream().map(Reserve::toDto).toList();
    }

    @PostMapping("/api/reserve/update") // 검증완료
    private ResponseReserveDTO update(@RequestBody UpdateRequestReserveDTO dto){
        return reserveService.updateReserve(dto);
    }

    @PostMapping("/api/reserve/delete") // 검증완료
    private Reserve delete(@RequestBody Long reserveId){
        return reserveService.deleteReserve(reserveId);
    }
}
