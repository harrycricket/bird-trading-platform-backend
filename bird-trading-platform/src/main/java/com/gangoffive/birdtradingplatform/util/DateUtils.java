package com.gangoffive.birdtradingplatform.util;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DateUtils {
    public static String formatLocalDateToString(LocalDate date) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM");
        return date.format(outputFormatter);
    }

    public static List<LocalDate> getAllDatePreviousWeek(int week) {
        List<LocalDate> localDateList = new ArrayList<>();
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        // Get the date of the previous week
        LocalDate previousWeekDate = currentDate.minusWeeks(week);
        // Get the start and end dates of the previous week
        LocalDate previousWeekStartDate = previousWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate previousWeekEndDate = previousWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        log.info("Previous week start date: {}", previousWeekStartDate);
        log.info("Previous week end date: {}", previousWeekEndDate);
        while (!previousWeekStartDate.isAfter(previousWeekEndDate)) {
            localDateList.add(previousWeekStartDate);
            previousWeekStartDate = previousWeekStartDate.plusDays(1);
        }
        return localDateList;
    }
}
