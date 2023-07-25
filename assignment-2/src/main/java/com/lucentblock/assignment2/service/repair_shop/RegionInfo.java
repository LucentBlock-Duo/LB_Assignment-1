package com.lucentblock.assignment2.service.repair_shop;

// This class will achieve the objects
// 1. find Region-Address-Info easily from DB.txt
// 2. validation for input region-keyword
// 3. parse for summarized-region-info

public class RegionInfo {
    private final String province;
    private final String summary;

    public RegionInfo(String province, String summary) {
        this.province = province;
        this.summary = summary;
    }

    boolean isValid(String keyword){
        return keyword.contains(province) || keyword.contains(summary);
    }
    // returns whether keyword contains province(ex. ChungCheungNamDo) or summary(ex. ChungNam)

    String regionParse(String keyword){
        return isValid(keyword) ? province : null; // if keyword "isValid", return correct province or null
    }
}
