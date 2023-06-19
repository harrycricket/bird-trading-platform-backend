package com.gangoffive.birdtradingplatform.enums;

public enum ColorChart {
    BIRD("hsl(232, 70%, 50%)"),
    FOOD("hsl(225, 70%, 50%)"),
    ACCESSORY("hsl(154, 70%, 50%)");

    private final String color;

    ColorChart(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
