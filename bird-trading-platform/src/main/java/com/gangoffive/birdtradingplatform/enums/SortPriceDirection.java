package com.gangoffive.birdtradingplatform.enums;

public enum SortPriceDirection {
    INCREASE ("Increase"),
    DECREASE ("Decrease");

    private String sortDirect;

    SortPriceDirection(String sortDirect) {
        this.sortDirect = sortDirect;
    }

    public String getSortDirect() {
        return sortDirect;
    }

    public void setSortDirect(String sortDirect) {
        this.sortDirect = sortDirect;
    }
}
