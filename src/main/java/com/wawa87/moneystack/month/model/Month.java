package com.wawa87.moneystack.month.model;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class Month {
    private Long id;
    private Long budgetId;
    private Year year;
    private java.time.Month month;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public java.time.Month getMonth() {
        return month;
    }

    public void setMonth(java.time.Month month) {
        this.month = month;
    }
}
