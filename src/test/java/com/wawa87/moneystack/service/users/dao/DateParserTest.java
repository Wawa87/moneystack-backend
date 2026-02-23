package com.wawa87.moneystack.service.users.dao;

import org.junit.jupiter.api.Test;

import javax.swing.text.DateFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateParserTest {
    @Test
    public void testDateParser() {
        String date0 = "2026-02-23 11:22:54.0";
        String date1 = "2026-02-23 11:22:54.01";
        String date2 = "2026-02-23 11:22:54.012";
        String date3 = "2026-02-23 11:22:54.0123";
        String date4 = "2026-02-23 11:22:54.01234";
        String date5 = "2026-02-23 11:22:54.012345";

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)  // 0-9 digits, optional
                .toFormatter();

        LocalDateTime parsed0 = LocalDateTime.parse(date0, formatter);
        LocalDateTime parsed1 = LocalDateTime.parse(date1, formatter);
        LocalDateTime parsed2 = LocalDateTime.parse(date2, formatter);
        LocalDateTime parsed3 = LocalDateTime.parse(date3, formatter);
        LocalDateTime parsed4 = LocalDateTime.parse(date4, formatter);
        LocalDateTime parsed5 = LocalDateTime.parse(date5, formatter);


    }
}
