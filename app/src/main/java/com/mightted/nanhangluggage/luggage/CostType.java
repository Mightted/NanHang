package com.mightted.nanhangluggage.luggage;

import org.litepal.crud.DataSupport;

import static android.R.attr.offset;

/**
 * Created by 晓深 on 2017/3/16.
 */

public class CostType extends DataSupport {
    int areaType;
    int overType;
    int cost;

    public CostType(int areaType, int overType, int cost) {
        this.areaType = areaType;
        this.overType = overType;
        this.cost = cost;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getOverType() {
        return overType;
    }

    public void setOverType(int overType) {
        this.overType = overType;
    }

}
