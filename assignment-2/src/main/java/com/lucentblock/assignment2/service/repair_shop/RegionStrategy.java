package com.lucentblock.assignment2.service.repair_shop;

import org.springframework.stereotype.Component;

import javax.swing.plaf.synth.Region;
import java.util.ArrayList;

@Component
public class RegionStrategy {
    private final ArrayList<RegionInfo> regions=new ArrayList<>();
    private RegionInfo currentRegion;
    RegionStrategy(){
        regions.add(new RegionInfo("충청남도","충남"));
        regions.add(new RegionInfo("대전광역시","대전"));
        regions.add(new RegionInfo("세종특별자치시","세종"));
        currentRegion=regions.get(0);
    }

    public RegionInfo switchStrategy(String keyword){
        RegionInfo region=
            regions.stream().filter(regionInfo -> regionInfo.isValid(keyword)).findAny().orElse(null);

        if(region!=null) setRegion(region);

        return getRegion();
    }

    public String getProvince(String keyword){
        return switchStrategy(keyword).regionParse(keyword);
    }

    private RegionInfo getRegion(){
        return this.currentRegion;
    }

    private void setRegion(RegionInfo region){
        this.currentRegion=region;
    }
}
