package com.wawa87.moneystack.service.app.util;

import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDTO;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;

import java.util.List;

public class DashboardSet {
    private UserDTO user;
    private Budget activeBudget;
    private List<TransactionDTO> transactions;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Budget getActiveBudget() {
        return activeBudget;
    }

    public void setActiveBudget(Budget activeBudget) {
        this.activeBudget = activeBudget;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }
}
