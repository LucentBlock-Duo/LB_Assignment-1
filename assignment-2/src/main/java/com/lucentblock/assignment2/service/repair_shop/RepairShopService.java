package com.lucentblock.assignment2.service.repair_shop;


import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.exception.LocationNotFoundException;
import com.lucentblock.assignment2.exception.RepairShopNotFoundException;
import com.lucentblock.assignment2.model.*;
import com.lucentblock.assignment2.repository.RepairShopRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairShopService {

    private final RepairShopRepository repairShopRepository;
    private final RepairShopMaker repairShopMaker;
    private final EntityManager em;

    public List<GPSResponseDTO> searchByAroundRepairShop(GPSRequestDTO gpsDto){
        BigDecimal distance=new BigDecimal("5000.0"); //

        BigDecimal latitude = gpsDto.getLatitude();
        BigDecimal longitude = gpsDto.getLongitude();

        List<RepairShopWithDistance> list=
                repairShopRepository.findRepairShopInDistanceRangeWithoutCity(latitude,longitude,distance); // 목록 뽑아오기

        return list.stream().map(RepairShopWithDistance::toDto).toList();
    }


    public GPSResponseDTO searchProximateRepairShop(GPSRequestDTO gpsDto) throws Exception {
        BigDecimal distance=new BigDecimal("5000.0"); // 기준을 어떻게 세울 것인가...............................

        BigDecimal latitude = gpsDto.getLatitude();
        BigDecimal longitude = gpsDto.getLongitude();
        String province = gpsDto.getProvince();

        List<RepairShopWithDistance> list=
                repairShopRepository.findRepairShopInDistanceRangeWithoutCity(latitude,longitude,distance); // 목록 뽑아오기

        if(list.size()<1) throw new RepairShopNotFoundException("가까운 매장이 없습니다.");

        list.sort((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));

        return repairShopRepository.findById(list.get(0).getId())
                .orElseThrow(()-> new RepairShopNotFoundException("가까운 매장 불러오기 오류")).toDto();
    }


    public GPSRequestDTO makeRequestDTO(Long userId,BigDecimal latitude, BigDecimal longitude,boolean userLocMode){
        User user = em.find(User.class, userId);
        if(userLocMode && user.getGpsAuthorized()){ // user Location search mode && user's GPS Authorization check
            BigDecimal userLatitude=user.getLatitude();
            BigDecimal userLongitude=user.getLongitude();
            return GPSRequestDTO.builder()
                    .latitude(userLatitude)
                    .latitude(userLongitude).build();
        }else{
            return GPSRequestDTO.builder() // else, Default Or input location-value selected
                    .latitude(latitude)
                    .longitude(longitude).build();
        }
    }


    public RepairShop findLocationById(Long id){
        return repairShopRepository.findById(id)
                .orElseThrow(()->new LocationNotFoundException("해당하는 지역을 찾을 수 없습니다.")); // 찾는 지역 없으면 예외
    }

    public RepairShop getRepairShopById(Long id) {
        return repairShopRepository.findById(id).orElseThrow(() -> new RepairShopNotFoundException(id.toString()));
    }


    public List<GPSResponseDTO> makeAuto(String keyword) throws IOException, URISyntaxException, ParseException, InterruptedException {
        return repairShopRepository.saveAll(repairShopMaker.makeLocationDataV2(keyword))
                .stream().map(RepairShop::toDto).toList();
    }

    public GPSResponseDTO makeManual(String address, String name) throws URISyntaxException, IOException, ParseException, InterruptedException {
        return repairShopRepository.save(repairShopMaker.makeLocationDataV1(address,name)).toDto();
    }
}

