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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairShopService {

    private final RepairShopRepository repairShopRepository;
    private final CountryLocationService countryLocationService;

    public List<ResponseRepairShopDTO> searchResult(RepairShopSearchRequestDTO requestDto) {
        CountryLocation location = countryLocationService.findLocationById(requestDto.getLocation_id());
        List<RepairShop> result =
                repairShopRepository.findByLocationAndKeyword(location, requestDto.getKeyword());

        if (result.size() < 1)
            throw new RepairShopNotFoundException("매장을 찾을 수 없습니다."); // 검색결과가 없다면 예외발생

        return result.stream().map(RepairShop::toDto).toList();
    }

    public boolean makeLocationDataV2() throws IOException, InterruptedException, URISyntaxException, ParseException {
        String keyword = "대전광역시 블루핸즈";
        int pagenum = 1;
        boolean isEnd = true;

        for (; isEnd; pagenum++) {
            String requestParameter = "?query=" + keyword + "&page=" + pagenum;

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://dapi.kakao.com/v2/local/search/keyword" + requestParameter))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "KakaoAK 3ebf3e1895b103b957da3434e0f9729c")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONArray document = getJSONArray(responseBody);
            JSONObject meta = getMetaJSON(responseBody);


            isEnd = Boolean.getBoolean(getJSONValue(meta, "is_end"));

            for (Object obj : document) {
                JSONObject json = ((JSONObject) obj);

                //String address=getJSONValue(json, "address_name"); // 대전 유성구 신성동 553
                String name = getJSONValue(json, "place_name"); // 현대자동차 블루핸즈 자명점
                String roadAddress = getJSONValue(json, "road_address_name"); // 대전 유성구 유성대로1184번길 71
                String x = getJSONValue(json, "x"); // 경도
                String y = getJSONValue(json, "y"); // 위도
                // 카카오맵으로 가져올 수 없는 것 : 우편번호, Open API

                int postNum = Integer.parseInt(countryLocationService.dataBuild(roadAddress)); // 우편번호



            }
        }

        return true;
    }

    private JSONObject getMetaJSON(String src) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(src); // JSON String을 JSON으로 변환
        return (JSONObject) jsonObject.get("meta");
    }

    private JSONArray getJSONArray(String src) throws ParseException {
        JSONObject jsonObject = (JSONObject) (new JSONParser().parse(src)); // JSON String을 JSON으로 변환
        return (JSONArray) jsonObject.get("DOCUMENTS"); // "RESULT" 부분만 추려냄
    }

    private String getJSONValue(JSONObject jsonObject, String key) {
        return String.valueOf(jsonObject.get(key));
    }
}

