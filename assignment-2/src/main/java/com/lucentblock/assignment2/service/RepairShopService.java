package com.lucentblock.assignment2.service;


import com.lucentblock.assignment2.entity.RepairShop;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.exception.LocationNotFoundException;
import com.lucentblock.assignment2.exception.RepairShopNotFoundException;
import com.lucentblock.assignment2.model.GPSRequestDTO;
import com.lucentblock.assignment2.model.GPSResponseDTO;
import com.lucentblock.assignment2.model.RepairShopSearchRequestDTO;
import com.lucentblock.assignment2.model.RepairShopWithDistance;
import com.lucentblock.assignment2.repository.RepairShopRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairShopService {

    private final RepairShopRepository repairShopRepository;
    private final EntityManager em;

    public List<GPSResponseDTO> searchResult(RepairShopSearchRequestDTO requestDto) {
        RepairShop location = repairShopRepository.findById(requestDto.getLocation_id())
                .orElseThrow(()-> new RepairShopNotFoundException("매장을 찾을 수 없습니다."));

        List<RepairShop> result = repairShopRepository.
                        findByLocationAndKeyword(location.getProvince(),location.getCity(),requestDto.getKeyword());



        if (result.size() < 1)
            throw new RepairShopNotFoundException("매장을 찾을 수 없습니다."); // 검색결과가 없다면 예외발생

        return result.stream().map(RepairShop::toDto).toList();
    }

    public List<GPSResponseDTO> searchByAroundRepairShop(GPSRequestDTO gpsDto){
        BigDecimal distance=new BigDecimal("5000.0"); // 기준을 어떻게 세울 것인가...............................

        BigDecimal latitude = gpsDto.getLatitude();
        BigDecimal longitude = gpsDto.getLongitude();

        List<RepairShopWithDistance> list=
                repairShopRepository.findRepairShopInDistanceRangeWithoutProvince(latitude,longitude,distance); // 목록 뽑아오기

        return list.stream().map(RepairShopWithDistance::toDto).toList();
    }


    public GPSResponseDTO searchProximateRepairShop(GPSRequestDTO gpsDto) throws Exception {
        BigDecimal distance=new BigDecimal("5000.0"); // 기준을 어떻게 세울 것인가...............................

        BigDecimal latitude = gpsDto.getLatitude();
        BigDecimal longitude = gpsDto.getLongitude();
        String province = gpsDto.getProvince();

        List<RepairShopWithDistance> list=
                repairShopRepository.findRepairShopInDistanceRangeWithoutProvince(latitude,longitude,distance); // 목록 뽑아오기

        if(list.size()<1) throw new RepairShopNotFoundException("가까운 매장이 없습니다.");

        list.sort((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));

        return repairShopRepository.findById(list.get(0).getId())
                .orElseThrow(()-> new RepairShopNotFoundException("가까운 매장 불러오기 오류")).toDto();
    }


    public GPSRequestDTO makeRequestDTO(Long userId,BigDecimal latitude, BigDecimal longitude,boolean userLocMode){
        User user = em.find(User.class, userId); // User 정보 가져오기
        if(userLocMode && true){ // 위도, 경도로 구하기
            BigDecimal userLatitude=null;
            BigDecimal userLongitude=null;

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
                .orElseThrow(()->new LocationNotFoundException("해당하는 지역을 찾을 수 없습니다.")); // 찾는 지역 없으면 예외
    }

    public boolean isEquals(String[] arr1, String[] arr2){
        if(arr2.length==5){
            return arr1[1].equals(arr2[0]) &&
                    arr1[3].equals(arr2[1]) &&
                    arr1[5].equals(arr2[2]) &&
                    arr1[8].equals(arr2[3]) &&
                    (arr2[4].split("-").length>1
                            ? (arr1[11]+"-"+arr1[12]).equals(arr2[4]) : arr1[11].equals(arr2[4]));
        }else{
            return arr1[1].equals(arr2[0]) &&
                    arr1[5].equals(arr2[1]) &&
                    arr1[8].equals(arr2[2]) &&
                    (arr2[3].split("-").length>1
                            ? (arr1[11]+"-"+arr1[12]).equals(arr2[3]) : arr1[11].equals(arr2[3]));
        }
    }
    public String[] dataBuild(String givenAddress) throws IOException {
        try {
            FileReader fileReader =
                    new FileReader("/Users/0tae1/IdeaProjects/LB_Assignment-2/assignment-2/src/main/resources/locationdata/세종특별자치시.txt");
            BufferedReader br = new BufferedReader(fileReader);

            String line = br.readLine(); // 1번째 줄 skip

            String[] givenAddr=givenAddress.split(" ");
            while ((line = br.readLine()) != null) {
                String[] args = line.split("\\|");
                givenAddr[0]="세종특별자치시";
                if(isEquals(args,givenAddr)){
                    fileReader.close();
                    return args;
                }
            }
        }catch (IOException e){
            log.error("File 불러오기 오류");
            return null;
        }catch (Exception e){
            log.error("Data build 오류");
            log.error("{}",e.getMessage());
            return null;
        }

        return null;
    }

    public boolean makeLocationDataV2() throws IOException, InterruptedException, URISyntaxException, ParseException {
        String keyword = URLEncoder.encode("세종 블루핸즈", StandardCharsets.UTF_8);
        int pagenum = 1;
        boolean isEnd = false;

        for (; !isEnd; pagenum++) {
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

            isEnd = Boolean.parseBoolean(getJSONValue(meta, "is_end"));

            for (Object obj : document) {
                JSONObject json = ((JSONObject) obj);

                String name = getJSONValue(json, "place_name"); // 현대자동차 블루핸즈 자명점
                String roadAddress = getJSONValue(json, "road_address_name"); // 대전 유성구 유성대로1184번길 71
                BigDecimal x = new BigDecimal(getJSONValue(json, "x")); // 경도
                BigDecimal y = new BigDecimal(getJSONValue(json, "y")); // 위도
                // 카카오맵으로 가져올 수 없는 것 : 우편번호

                if(!roadAddress.split(" ")[0].equals("세종특별자치시")) continue;

                String[] info = dataBuild(roadAddress); // 우편번호

                int postNum=Integer.parseInt(info[0]);

                String city=info[3];
                String province=info[1]; // 대전광역시에서 대전을 추출하기 위함
                String roadAdd=info[8];

                RepairShop repairShop =
                        RepairShop.builder()
                                .name(name)
                                .city(city)
                                .province(province)
                                .address(roadAddress+" "+name)
                                .roadAddress(roadAdd)
                                .postNum(postNum)
                                .latitude(y)
                                .createdAt(LocalDateTime.now())
                                .longitude(x).build();

                log.info("location save : {}",repairShopRepository.save(repairShop));
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
        return (JSONArray) jsonObject.get("documents"); // "RESULT" 부분만 추려냄
    }

    private String getJSONValue(JSONObject jsonObject, String key) {
        return String.valueOf(jsonObject.get(key));
    }
}

