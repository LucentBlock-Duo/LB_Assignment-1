package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.model.CreateRequestReserveDTO;
import com.lucentblock.assignment2.model.ResponseReserveDTO;
import com.lucentblock.assignment2.model.UpdateRequestReserveDTO;
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

    @PostMapping("/api/reserve/create") // 검증완료
    private ResponseReserveDTO create(@RequestBody CreateRequestReserveDTO dto){
        return reserveService.createReserve(dto);
    }

    @GetMapping("/api/reserve/read") // 검증완료
    private List<ResponseReserveDTO> read(@RequestParam Long carId){ // 현재 고객 ID 기반으로 모든 Car 가져오기 or 그냥 car 가져오기
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
