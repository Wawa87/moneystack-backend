package com.wawa87.moneystack.service.system.transaction.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private Long monthId;
    private String category;
    private String subcategory;
    private String description;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonthId() {
        return monthId;
    }

    public void setMonthId(Long monthId) {
        this.monthId = monthId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
