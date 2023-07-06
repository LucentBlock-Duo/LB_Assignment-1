package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.CountryLocation;
import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.exception.RepairShopNotFoundException;
import com.lucentblock.assignment2.model.RepairShopSearchRequestDTO;
import com.lucentblock.assignment2.model.ResponseRepairShopDTO;
import com.lucentblock.assignment2.repository.RepairShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairShopService {

    private final RepairShopRepository repairShopRepository;
    private final CountryLocationService countryLocationService;

    public List<ResponseRepairShopDTO> searchResult(RepairShopSearchRequestDTO requestDto) {
        CountryLocation location= countryLocationService.findLocationById(requestDto.getLocation_id());
        List<RepairShop> result =
                repairShopRepository.findByLocationAndKeyword(location, requestDto.getKeyword());

        if(result.size()<1)
            throw new RepairShopNotFoundException("매장을 찾을 수 없습니다."); // 검색결과가 없다면 예외발생

        return result.stream().map(RepairShop::toDto).toList();
    }

    public boolean makeLocationData() throws IOException {
        if(countryLocationService.dataBuild()){
            return true;
        }else{
            log.error("데이터 빌드 실패");
            return false;
        }
    }
}
