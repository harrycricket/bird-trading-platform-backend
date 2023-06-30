package com.gangoffive.birdtradingplatform.wrapper;

import lombok.Data;

import java.util.List;

@Data
public class PageNumberWrapper<T> {
    private List<T> lists;

    private int pageNumber;

    private long totalElement;

    public PageNumberWrapper() {
    }

    public PageNumberWrapper(List<T> lists, int pageNumber, long totalElement) {
        this.lists = lists;
        this.pageNumber = pageNumber;
        this.totalElement = totalElement;
    }

    public PageNumberWrapper(List<T> lists, int pageNumber) {
        this.lists = lists;
        this.pageNumber = pageNumber;
    }
}
