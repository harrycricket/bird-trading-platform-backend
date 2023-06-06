package com.gangoffive.birdtradingplatform.util;

import java.util.Arrays;
import java.util.List;

public class MyUtils {

    public static  List<String> toLists(String str, String patternSplit ){
        return Arrays.stream(str.split(patternSplit)).toList();
    }
}
