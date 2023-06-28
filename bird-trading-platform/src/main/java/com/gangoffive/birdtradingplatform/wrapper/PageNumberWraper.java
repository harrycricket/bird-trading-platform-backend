package com.gangoffive.birdtradingplatform.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class PageNumberWraper<T> {
    private List<T> lists;

    private int pageNumber;

    private long totalElement;

    public PageNumberWraper() {
    }

    public PageNumberWraper(List<T> lists, int pageNumber, long totalElement) {
        this.lists = lists;
        this.pageNumber = pageNumber;
        this.totalElement = totalElement;
    }

    public PageNumberWraper(List<T> lists, int pageNumber) {
        this.lists = lists;
        this.pageNumber = pageNumber;
    }
}
