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
        BigDecimal distance=new BigDecimal("5000.0");

        BigDecimal latitude = gpsDto.getLatitude();
        BigDecimal longitude = gpsDto.getLongitude();

        List<RepairShopWithDistance> list=
                repairShopRepository.findRepairShopInDistanceRangeWithoutCity(latitude,longitude,distance);

        return list.stream().map(RepairShopWithDistance::toDto).toList();
    }


    public GPSResponseDTO searchProximateRepairShop(GPSRequestDTO gpsDto) throws Exception {
        BigDecimal distance=new BigDecimal("5000.0");

        BigDecimal latitude = gpsDto.getLatitude();
        BigDecimal longitude = gpsDto.getLongitude();
        String province = gpsDto.getProvince();

        List<RepairShopWithDistance> list=
                repairShopRepository.findRepairShopInDistanceRangeWithoutCity(latitude,longitude,distance);

        if(list.size()<1) throw new RepairShopNotFoundException("가까운 매장이 없습니다.");

        list.sort((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));

        return repairShopRepository.findById(list.get(0).getId())
                .orElseThrow(()-> new RepairShopNotFoundException("가까운 매장 불러오기 오류")).toDto();
    }



    /*
    * Make RequestDTO about GPSInfo, varies by search mode.
    *
    * Mode 1 : User-Surrounding-Location-Search, using users current latitude and longitude.
    * When Mode 1 is enabled, then userLocMode is true.
    *
    * Mode 2 : Static-Location-Search, using input latitude and longitude.
    * When Mode 2 is enabled, then userLocMode is false.
    *
    * Default latitude & longitude value are already set
    */
    public GPSRequestDTO makeRequestDTO(Long userId,BigDecimal latitude, BigDecimal longitude,boolean userLocMode){
        User user = em.find(User.class, userId);
        if(userLocMode && user.getGpsAuthorized()){
            BigDecimal userLatitude=user.getLatitude();
            BigDecimal userLongitude=user.getLongitude();
            return GPSRequestDTO.builder()
                    .latitude(userLatitude)
                    .latitude(userLongitude).build();
        }else{
            return GPSRequestDTO.builder()
                    .latitude(latitude)
                    .longitude(longitude).build();
        }
    }


    public RepairShop findLocationById(Long id){
        return repairShopRepository.findById(id)
                .orElseThrow(()->new LocationNotFoundException("해당하는 지역을 찾을 수 없습니다."));
    }

    public RepairShop getRepairShopById(Long id) {
        return repairShopRepository.findById(id).orElseThrow(() -> new RepairShopNotFoundException(id.toString()));
    }


    /*
    * For add list of repair-shops location info to DB, already exists in reality
    */
    public List<GPSResponseDTO> makeAuto(String keyword) throws IOException, URISyntaxException, ParseException, InterruptedException {
        return repairShopRepository.saveAll(repairShopMaker.makeLocationDataV2(keyword))
                .stream().map(RepairShop::toDto).toList();
    }

    /*
     * For add repair-shop location info to DB, will be added by administrator
     */
    public GPSResponseDTO makeManual(String address, String name) throws URISyntaxException, IOException, ParseException, InterruptedException {
        return repairShopRepository.save(repairShopMaker.makeLocationDataV1(address,name)).toDto();
    }
}

