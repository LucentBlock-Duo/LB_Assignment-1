package com.lucentblock.assignment2.service.repair_shop;


import com.lucentblock.assignment2.entity.RepairShop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepairShopMaker {
    private final RegionStrategy regionStrategy;

    /*
    * Compare input road-address to DB-full-address
    *
    * roadAddress[0] : province
    * roadAddress[1] : city or town, village
    * roadAddress[2] : roadAddress
    * roadAddress[3] : building-number, containing hyphen
    *                   OR roadAddressName (having 5 argument roadAddress, last letter is "Do")
    * roadAddress[4] : building-number (Only having 5 argument roadAddress, last letter is "Do")
    *
    * DBArgs[1] : province
    * DBArgs[3] : city
    * DBArgs[5] : town or village
    * DBArgs[8] : roadAddressName
    * DBArgs[11] : building number
    * DBArgs[12] : building number-second
    *
    * others in DBArgs elements not be considered
    */
    public boolean isEquals(String[] DBArgs, String[] roadAddress) {
        if(roadAddress[0].equals("세종특별자치시")){ // This suit for "Sejong"
            return DBArgs[1].equals(roadAddress[0]) &&
                    DBArgs[5].equals(roadAddress[1]) &&
                    DBArgs[8].equals(roadAddress[2]) &&
                    (roadAddress[3].split("-").length > 1 // Compare building-number, and find whether hyphen exists
                            ? (DBArgs[11] + "-" + DBArgs[12]).equals(roadAddress[3]) : DBArgs[11].equals(roadAddress[3]));
        } else if (roadAddress.length == 5) {  // This suit for "ChungCheongNamDo"
            return DBArgs[1].equals(roadAddress[0]) &&
                    DBArgs[3].equals(roadAddress[1]) &&
                    DBArgs[5].equals(roadAddress[2]) &&
                    DBArgs[8].equals(roadAddress[3]) &&
                    (roadAddress[4].split("-").length > 1
                            ? (DBArgs[11] + "-" + DBArgs[12]).equals(roadAddress[4]) : DBArgs[11].equals(roadAddress[4]));
        } else {                         // This suit for "Daejeon"
            return DBArgs[1].equals(roadAddress[0]) &&
                    DBArgs[3].equals(roadAddress[1]) &&
                    DBArgs[8].equals(roadAddress[2]) &&
                    (roadAddress[3].split("-").length > 1
                            ? (DBArgs[11] + "-" + DBArgs[12]).equals(roadAddress[3]) : DBArgs[11].equals(roadAddress[3]));
        }
    }

    public String[] dataBuild(String givenAddress) throws IOException {
        String filename=regionStrategy.getProvince(givenAddress); // Get ${province}.txt file name
        try {
            FileReader fileReader =
                    new FileReader("/Users/0tae1/IdeaProjects/LB_Assignment-2/assignment-2" +
                            "/src/main/resources/locationdata/"+filename+".txt");

            BufferedReader br = new BufferedReader(fileReader);

            String line = br.readLine(); // Skip for first line

            String[] givenAddr = givenAddress.split(" "); // Convert parameter string to string-array
            givenAddr[0] = filename; // Equalize province parameter to filename

            while ((line = br.readLine()) != null) {
                String[] args = line.split("\\|");

                if (isEquals(args, givenAddr)) {
                    fileReader.close();
                    return args;
                }
            }
        } catch (IOException e) {
            log.error("File 불러오기 오류 : {}",e.getMessage());
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
    } // KaKao Map REST API Request By RoadAddress

    private HttpRequest getRequestForKeywordSearch(String param) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI("https://dapi.kakao.com/v2/local/search/keyword" + param))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "KakaoAK 3ebf3e1895b103b957da3434e0f9729c")
                .GET()
                .build();
    } // KaKao Map REST API Request By Keyword

    private HttpResponse<String> getResponse(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } // Get client and Send request from localhost, and get response


    /*
     * Use for create a repair-shop entity
     * Require CORRECT roadAddress and repair-shop's name for add
     */
    public RepairShop makeLocationDataV1(String roadAddress,String name) throws URISyntaxException, IOException, InterruptedException, ParseException {
        String param = URLEncoder.encode(roadAddress, StandardCharsets.UTF_8); // Encoding URL For Korean language
        HttpRequest request = getRequestForRoadAddressSearch("?query=" + param);
        String responseBody = getResponse(request).body();
        JSONArray document = getJSONArray(responseBody);

        if(document.size()<1){
            log.error("Location NOT_FOUND : {}",roadAddress);
            return null;
        }

        JSONObject json=(JSONObject) document.get(0);

        BigDecimal x = new BigDecimal(getJSONValue(json, "x")); // longitude
        BigDecimal y = new BigDecimal(getJSONValue(json, "y")); // latitude

        // Get Specific Address From DB file, ${province}.txt
        // Input : roadAddress
        String[] info = dataBuild(roadAddress);

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


    // Use for create All repair-shop entities in the region
    // Require a keyword only
    public List<RepairShop> makeLocationDataV2(String keywordValue) throws IOException, InterruptedException, URISyntaxException, ParseException {

        int pagenum = 1;
        boolean isEnd = false;

        List<RepairShop> repairShopList = new ArrayList<>();
        HashSet<String> repairShopSet=new HashSet<>();
        for (; !isEnd; pagenum++) {
            String param = URLEncoder.encode(keywordValue, StandardCharsets.UTF_8); // Encoding URL For Korean language
            HttpRequest request = getRequestForKeywordSearch("?query=" + param + "&page=" + pagenum);
            String responseBody = getResponse(request).body();

            JSONArray document = getJSONArray(responseBody);
            JSONObject meta = getMetaJSON(responseBody);

            isEnd = Boolean.parseBoolean(getJSONValue(meta, "is_end"));

            for (Object obj : document) {
                JSONObject json = ((JSONObject) obj);

                String name = getJSONValue(json, "place_name");
                String roadAddress = getJSONValue(json, "road_address_name");
                BigDecimal x = new BigDecimal(getJSONValue(json, "x"));
                BigDecimal y = new BigDecimal(getJSONValue(json, "y"));

                String provinceFromRoadAddress=roadAddress.split(" ")[0];

                RegionInfo regionInfo =
                    regionStrategy.switchStrategy(provinceFromRoadAddress);

                // Check whether JSON-data is correct value.
                if (regionInfo.isValid(provinceFromRoadAddress)) continue;

                String[] info = dataBuild(roadAddress);

                int postNum = Integer.parseInt(info[0]);

                String province = info[1];
                String city = info[3];
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

                if(!repairShopSet.contains(roadAddress)){
                    repairShopSet.add(roadAddress);
                    repairShopList.add(repairShop);
                }
            }
        }

        return repairShopList;
    }

    private JSONObject getMetaJSON(String src) throws ParseException {
        JSONObject jsonObject = (JSONObject) (new JSONParser().parse(src));
        return (JSONObject) jsonObject.get("meta");
    }

    private JSONArray getJSONArray(String src) throws ParseException {
        JSONObject jsonObject = (JSONObject) (new JSONParser().parse(src));
        return (JSONArray) jsonObject.get("documents");
    }

    private String getJSONValue(JSONObject jsonObject, String key) {
        return String.valueOf(jsonObject.get(key));
    }

}
