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



    @Transactional
    public boolean dataBuild() throws IOException {
        try {
            FileReader fileReader =
                    new FileReader("/Users/0tae1/IdeaProjects/LB_Assignment-2/assignment-2/src/main/resources/locationdata/대전광역시.txt");
            BufferedReader br = new BufferedReader(fileReader);

            List<CountryLocation> database = new ArrayList<>();
            HashMap<String, String> dataMap = new HashMap<>();

            for (CountryLocation location : database) {
                dataMap.put(location.getCity(), location.getProvince());
            }

            String line = br.readLine(); // 1번째 줄 skip

            while ((line = br.readLine()) != null) {
                String[] args = line.split("\\|");

                if (dataMap.containsKey(args[3])) continue;

                CountryLocation data =
                        CountryLocation.builder()
                                .province(args[1])
                                .city(args[3])
                                .createdAt(LocalDateTime.now())
                                .build();

                dataMap.put(args[3], args[1]);
                countryLocationRepository.save(data);
            }
        }catch (IOException e){
            log.error("File 불러오기 오류");
            return false;
        }catch (Exception e){
            log.error("Data build 오류");
            return false;
        }

        return true;
    }
}
