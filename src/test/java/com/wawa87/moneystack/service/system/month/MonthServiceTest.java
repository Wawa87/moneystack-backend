package com.wawa87.moneystack.service.system.month;

import com.wawa87.moneystack.month.MonthService;
import com.wawa87.moneystack.month.model.Month;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class MonthServiceTest {
    private MonthService monthService;

    @Test
    public void testGetMonthsByBudgetId() {
        Month month0 = new Month();
        month0.setId(Long.valueOf(0));
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.MARCH);

        Month month1 = new Month();
        month1.setId(Long.valueOf(1));
        month1.setYear(Year.of(2026));
        month1.setMonth(java.time.Month.JULY);

        Month month2 = new Month();
        month2.setId(Long.valueOf(2));
        month2.setYear(Year.of(2022));
        month2.setMonth(java.time.Month.OCTOBER);

        Month month3 = new Month();
        month3.setId(Long.valueOf(3));
        month3.setYear(Year.of(2024));
        month3.setMonth(java.time.Month.APRIL);

        List<Month> months = new ArrayList<>();
        months.add(month0);
        months.add(month1);
        months.add(month2);
        months.add(month3);

        months = MonthService.sortMonthsDesc(months);

        Assertions.assertEquals(1, months.get(0).getId());
        Assertions.assertEquals(0, months.get(1).getId());
        Assertions.assertEquals(3, months.get(2).getId());
        Assertions.assertEquals(2, months.get(3).getId());
    }
}
