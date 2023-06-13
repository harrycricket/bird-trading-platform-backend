package com.gangoffive.birdtradingplatform.enums;

public enum ColorPieChart {
    BIRD("hsl(232, 70%, 50%)"),
    FOOD("hsl(225, 70%, 50%)"),
    ACCESSORY("hsl(154, 70%, 50%)");

    private final String color;

    ColorPieChart(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
