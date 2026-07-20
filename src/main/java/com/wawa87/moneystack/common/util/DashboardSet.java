package com.wawa87.moneystack.common.util;

import com.wawa87.moneystack.budget.model.Budget;
import com.wawa87.moneystack.transaction.dao.TransactionDTO;
import com.wawa87.moneystack.user.dao.UserDTO;

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
