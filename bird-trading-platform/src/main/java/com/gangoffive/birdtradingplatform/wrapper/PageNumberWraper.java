package com.gangoffive.birdtradingplatform.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageNumberWraper<T> {
    private List<T> lists;

    private int pageNumber;
}
