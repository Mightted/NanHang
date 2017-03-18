package com.mightted.nanhangluggage.luggage;


import org.litepal.crud.DataSupport;

import static android.R.attr.id;

/**
 * Created by 晓深 on 2017/3/13.
 */

public class Luggage extends DataSupport {


    private int id;
    private int areaType;  //区域类型
    private String type;  //舱位类型，用字符串记录
    private int sum;  //免费行李数上限
    private float weight;  //每个免费行李的重量上限
    private float maxLength;  //免费行李的长宽高之和上限

    public Luggage(int id,int areaType, String type, int sum, float weight, float maxLength) {
        this.id = id;
        this.areaType = areaType;
        this.type = type;
        this.sum = sum;
        this.weight = weight;
        this.maxLength = maxLength;

    }

    public Luggage() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }

    public float getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(float maxLength) {
        this.maxLength = maxLength;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
