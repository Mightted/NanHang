package com.mightted.nanhangluggage.luggage;

/**
 * Created by 晓深 on 2017/3/15.
 */

public class Package {
    private float weight;
    private float length;
    private float width;
    private float height;

    public Package(float weight, float length, float width, float height) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public Package() {}

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
