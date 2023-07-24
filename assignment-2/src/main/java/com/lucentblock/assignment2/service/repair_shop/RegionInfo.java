package com.lucentblock.assignment2.service.repair_shop;

public class RegionInfo {
    private final String province; // 충청남도
    private final String summary; // 충남

    public RegionInfo(String province, String summary) {
        this.province = province;
        this.summary = summary;
    }

    boolean isValid(String keyword){
        return keyword.contains(province) || keyword.contains(summary);
    }

    String regionParse(String keyword){
        return isValid(keyword) ? province : null;
    }

}
