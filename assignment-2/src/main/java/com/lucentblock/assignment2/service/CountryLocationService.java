package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.CountryLocation;
import com.lucentblock.assignment2.exception.LocationNotFoundException;
import com.lucentblock.assignment2.repository.CountryLocationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryLocationService {
    private final CountryLocationRepository countryLocationRepository;

    public CountryLocation findLocationById(Long id){
        return countryLocationRepository.findById(id)
                .orElseThrow(()->new LocationNotFoundException("해당하는 지역을 찾을 수 없습니다.")); // 찾는 지역 없으면 예외
    }



    public String dataBuild(String givenAddress) throws IOException {
        try {
            FileReader fileReader =
                    new FileReader("/Users/0tae1/IdeaProjects/LB_Assignment-2/assignment-2/src/main/resources/locationdata/대전광역시.txt");
            BufferedReader br = new BufferedReader(fileReader);

            String line = br.readLine(); // 1번째 줄 skip

            while ((line = br.readLine()) != null) {
                String[] args = line.split("\\|");
                String address=args[1]+args[3]+args[8]+args[11]; // 시, 구, 도로명, 건물번호 본번(대전 유성구 대학로159번길 6)
                if(address.equals(givenAddress)) return args[0];
            }
        }catch (IOException e){
            log.error("File 불러오기 오류");
            return null;
        }catch (Exception e){
            log.error("Data build 오류");
            return null;
        }

        return null;
    }
}
