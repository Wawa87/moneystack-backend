package com.wawa87.moneystack.month;

import com.wawa87.moneystack.month.dao.MonthDAO;
import com.wawa87.moneystack.month.model.Month;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

public class MonthService {
    private static final Logger logger = LoggerFactory.getLogger(MonthService.class);

    private MonthDAO monthDAO;

    public MonthService(MonthDAO monthDAO) {
        this.monthDAO = monthDAO;
    }

    public List<Month> getMonthsByBudgetId(Long budgetId) {
        List<Month> months = this.monthDAO.findByBudgetId(budgetId);
//        months.sort(sortMonth);
        return months;
    }

    public static List<Month> sortMonthsDesc(List<Month> months) {
        months.sort(createMonthComparator().reversed());
        return months;
    }

    private static Comparator<Month> createMonthComparator() {
        return Comparator.comparing(Month::getYear)
                .thenComparing(Month::getMonth);
    }

    public void saveMonth(Month month) {
        monthDAO.save(month);
    }
}
