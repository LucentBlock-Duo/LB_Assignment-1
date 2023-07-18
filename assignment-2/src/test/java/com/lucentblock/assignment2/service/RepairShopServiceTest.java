package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.exception.LocationNotFoundException;
import com.lucentblock.assignment2.model.RepairShopSearchRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
public class RepairShopServiceTest {
    @InjectMocks
    private RepairShopService repairShopService;

    @Test
    @DisplayName("검색어를 찾기 이전에, 지역 ID가 없다면 예외를 발생시킨다.")
    public void repairShopSearchServiceWithLocationException() {
        RepairShopSearchRequestDTO happy =
                RepairShopSearchRequestDTO.builder()
                .keyword("대덕")
                .location_id(1L)
                .build();


        assertThatThrownBy(() -> repairShopService.searchResult(happy))
                .isInstanceOf(LocationNotFoundException.class);

//        given(countryLocationService.findLocationById(happy.getLocation_id()))
//                .willThrow(LocationNotFoundException.class);
//
//        assertThrows(LocationNotFoundException.class, () -> repairShopService.searchResult(happy));
    }

//    @Test
//    @DisplayName("해당 지역에 키워드가 맞는 정비소 명이 없다면 예외를 발생시킨다.")
//
//    @Test
//    @DisplayName("해당 지역에 키워드가 맞는 정비소 명이 있다면 반환한다.")
}
