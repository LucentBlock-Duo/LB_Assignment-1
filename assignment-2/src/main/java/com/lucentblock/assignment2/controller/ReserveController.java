package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.RepairStatus;
import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.model.*;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.car.Car;
import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.model.CreateRequestReserveDTO;
import com.lucentblock.assignment2.model.ResponseReserveDTO;
import com.lucentblock.assignment2.exception.BalanceNotEnoughException;
import com.lucentblock.assignment2.service.UserService;
import com.lucentblock.assignment2.service.car.CarService;
import com.lucentblock.assignment2.service.RepairShopService;
import com.lucentblock.assignment2.service.ReserveService;
import com.lucentblock.assignment2.service.item.ItemDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
@RequiredArgsConstructor
@Slf4j
public class ReserveController {
    private final ReserveService reserveService;
    private final RepairShopService repairShopService;
    private final CarService carService;
    private final ItemDetailService itemDetailService;
    private final UserService userService;

    @PostMapping
    @Transactional
    public ResponseEntity<List<ResponseReserveDTO>> createReserve(@RequestBody List<CreateRequestReserveDTO> createRequestReserveDTOs) {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        return ResponseEntity.ok(createRequestReserveDTOs.stream()
                .map(dto -> {
                    RepairShop repairShop = repairShopService.getRepairShopById(dto.getRepair_shop_id());
                    Car car = carService.getCarById(dto.getCar_id());
                    ItemDetail item = itemDetailService.getItemById(dto.getItem_detail_id());

                    if (!car.getUser().getEmail().equals(user.getEmail())) {
                        log.info(user.getEmail() + " 이 " + car.getUser().getEmail() + " 에 접근을 시도했습니다.");
                        throw new AccessDeniedException("잘못된 접근");
                    }
                    if (user.getBalance() >= item.getPrice()) {
                        user.setBalance(user.getBalance() - item.getPrice());
                        return reserveService.createReserve(repairShop, car, dto.getDate(), dto.getStart_time(), item).toDto();
                    } else {
                        throw new BalanceNotEnoughException(user.getEmail() + " 의 잔고가 " + (item.getPrice() - user.getBalance()) + " 만큼 부족합니다.");
                    }
                }).toList());
    }

    @GetMapping
    public ResponseEntity fetchReservesByUser() {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        return ResponseEntity.ok(reserveService.getReservesByUser(user).stream()
                .map(reserve -> reserve.toDto()).toList());
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity deleteReserves(@RequestParam("ids") List<Long> ids) {
        // get reservesById 로 레포지토리에서 꺼내온 다음
        // 요청한 Reserve 들이 모두 현재 로그인한 사용자와 맞는지 확인하고
        // 로그인한 사용자가 맞다면, deleted_at 을 채워넣고, 아니라면 Exception 을 발생시킨다.
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(
                reserveService.deleteReserves(ids, username).stream()
                        .map(reserve -> reserve.toDto())
                        .toList()
        );
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
