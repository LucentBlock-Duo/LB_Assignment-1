package com.lucentblock.assignment2.service.repair_shop;


import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.exception.RepairShopNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RepairShopMaker {
    private final HttpClient httpClient;

    public boolean isEquals(String[] arr1, String[] arr2) {
        if (arr2.length == 5) {
            return arr1[1].equals(arr2[0]) &&
                    arr1[3].equals(arr2[1]) &&
                    arr1[5].equals(arr2[2]) &&
                    arr1[8].equals(arr2[3]) &&
                    (arr2[4].split("-").length > 1
                            ? (arr1[11] + "-" + arr1[12]).equals(arr2[4]) : arr1[11].equals(arr2[4]));
        } else {
            return arr1[1].equals(arr2[0]) &&
                    arr1[5].equals(arr2[1]) &&
                    arr1[8].equals(arr2[2]) &&
                    (arr2[3].split("-").length > 1
                            ? (arr1[11] + "-" + arr1[12]).equals(arr2[3]) : arr1[11].equals(arr2[3]));
        }
    }

    public String[] dataBuild(String givenAddress) throws IOException {
        try {
            FileReader fileReader =
                    new FileReader("./src/main/resources/locationdata/세종특별자치시.txt");
            BufferedReader br = new BufferedReader(fileReader);

            String line = br.readLine(); // 1번째 줄 skip

            String[] givenAddr = givenAddress.split(" ");
            while ((line = br.readLine()) != null) {
                String[] args = line.split("\\|");
                givenAddr[0] = "세종특별자치시";
                if (isEquals(args, givenAddr)) {
                    fileReader.close();
                    return args;
                }
            }
        } catch (IOException e) {
            log.error("File 불러오기 오류");
            return null;
        } catch (Exception e) {
            log.error("Data build 오류");
            log.error("{}", e.getMessage());
            return null;
        }

        return null;
    }

    private HttpRequest getRequestForRoadAddressSearch(String param) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI("https://dapi.kakao.com/v2/local/search/address.json" + param))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "KakaoAK 3ebf3e1895b103b957da3434e0f9729c")
                .GET()
                .build();
    } // 수정 요망

    private HttpRequest getRequestForKeywordSearch(String param) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI("https://dapi.kakao.com/v2/local/search/keyword" + param))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "KakaoAK 3ebf3e1895b103b957da3434e0f9729c")
                .GET()
                .build();
    }

    private HttpResponse<String> getResponse(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public RepairShop makeLocationDataV1(String roadAddress,String name) throws URISyntaxException, IOException, InterruptedException, ParseException {
        String param = URLEncoder.encode(roadAddress, StandardCharsets.UTF_8);
        HttpRequest request = getRequestForKeywordSearch("?query=" + param);
        String responseBody = getResponse(request).body();
        JSONArray document = getJSONArray(responseBody);

        if(document.size()<1){
            log.error("Location NOT_FOUND : {}",roadAddress);
            return null;
        }

        JSONObject json=(JSONObject) document.get(0);

        BigDecimal x = new BigDecimal(getJSONValue(json, "x")); // 경도
        BigDecimal y = new BigDecimal(getJSONValue(json, "y")); // 위도
        // 카카오맵으로 가져올 수 없는 것 : 우편번호

        String[] info = dataBuild(roadAddress); // 우편번호

        int postNum = Integer.parseInt(info[0]);
        String province = info[1];
        String city = info[3];
        String roadAdd = info[8];

       return RepairShop.builder()
                        .name(name)
                        .city(city)
                        .province(province)
                        .address(roadAddress + " " + name)
                        .roadAddress(roadAdd)
                        .postNum(postNum)
                        .latitude(y)
                        .createdAt(LocalDateTime.now())
                        .longitude(x).build();
    }

    public List<RepairShop> makeLocationDataV2() throws IOException, InterruptedException, URISyntaxException, ParseException {
        String keyword = URLEncoder.encode("세종 블루핸즈", StandardCharsets.UTF_8);
        int pagenum = 1;
        boolean isEnd = false;

        List<RepairShop> repairShopList = new ArrayList<>();

        for (; !isEnd; pagenum++) {
            String requestParameter = "?query=" + keyword + "&page=" + pagenum;

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

            isEnd = Boolean.parseBoolean(getJSONValue(meta, "is_end"));


            for (Object obj : document) {
                JSONObject json = ((JSONObject) obj);

                String name = getJSONValue(json, "place_name"); // 현대자동차 블루핸즈 자명점
                String roadAddress = getJSONValue(json, "road_address_name"); // 대전 유성구 유성대로1184번길 71
                BigDecimal x = new BigDecimal(getJSONValue(json, "x")); // 경도
                BigDecimal y = new BigDecimal(getJSONValue(json, "y")); // 위도
                // 카카오맵으로 가져올 수 없는 것 : 우편번호

                if (!roadAddress.split(" ")[0].equals("세종특별자치시")) continue;

                String[] info = dataBuild(roadAddress); // 우편번호

                int postNum = Integer.parseInt(info[0]);

                String city = info[3];
                String province = info[1]; // 대전광역시에서 대전을 추출하기 위함
                String roadAdd = info[8];

                RepairShop repairShop =
                        RepairShop.builder()
                                .name(name)
                                .city(city)
                                .province(province)
                                .address(roadAddress + " " + name)
                                .roadAddress(roadAdd)
                                .postNum(postNum)
                                .latitude(y)
                                .createdAt(LocalDateTime.now())
                                .longitude(x).build();

                repairShopList.add(repairShop);
            }
        }

        return repairShopList;
    }

    private JSONObject getMetaJSON(String src) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(src); // JSON String을 JSON으로 변환
        return (JSONObject) jsonObject.get("meta");
    }

    private JSONArray getJSONArray(String src) throws ParseException {
        JSONObject jsonObject = (JSONObject) (new JSONParser().parse(src)); // JSON String을 JSON으로 변환
        return (JSONArray) jsonObject.get("documents"); // "RESULT" 부분만 추려냄
    }

    private String getJSONValue(JSONObject jsonObject, String key) {
        return String.valueOf(jsonObject.get(key));
    }

}
