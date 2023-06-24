package com.gangoffive.birdtradingplatform.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class PageNumberWraper<T> {
    private List<T> lists;

    private int pageNumber;

    private long totalProduct;

    public PageNumberWraper() {
    }

    public PageNumberWraper(List<T> lists, int pageNumber, long totalProduct) {
        this.lists = lists;
        this.pageNumber = pageNumber;
        this.totalProduct = totalProduct;
    }

    public PageNumberWraper(List<T> lists, int pageNumber) {
        this.lists = lists;
        this.pageNumber = pageNumber;
    }
}
