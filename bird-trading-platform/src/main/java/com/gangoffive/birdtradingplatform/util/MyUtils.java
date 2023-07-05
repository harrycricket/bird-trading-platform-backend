package com.gangoffive.birdtradingplatform.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MyUtils {
    public static  List<String> toLists(String str, String patternSplit ){
        return Arrays.stream(str.split(patternSplit)).toList();
    }

    public static int generateSixRandomNumber() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public static void main(String[] args) {
        System.out.println(MyUtils.generateSixRandomNumber());
    }

    public static List<String> splitStringToList(String str, String pattern) {
        if(str != null) {
            return Arrays.asList(str.split(pattern));
        }
        return null;
    }
}
