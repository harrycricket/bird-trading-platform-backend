package com.gangoffive.birdtradingplatform.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String formatLocalDateToString(LocalDate date) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return date.format(outputFormatter);
    }
}
