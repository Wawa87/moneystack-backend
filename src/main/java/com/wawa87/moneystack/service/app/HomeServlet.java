package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.system.budget.BudgetService;
import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.month.MonthService;
import com.wawa87.moneystack.service.system.month.model.Month;
import com.wawa87.moneystack.service.system.transaction.TransactionService;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HomeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
    UserService userService;
    BudgetService budgetService;
    MonthService monthService;
    TransactionService transactionService;

    public HomeServlet(UserService userService, BudgetService budgetService, MonthService monthService, TransactionService transactionService) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.monthService = monthService;
        this.transactionService = transactionService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<User> userRes = this.userService.getUser((String) request.getAttribute("subject"));
        if (userRes.isPresent()) {
            // Get User data.
            User currentUser = userRes.get();
            AppUser appUser = new AppUser();
            appUser.setUserId(currentUser.getId());
            appUser.setUsername(currentUser.getUsername());
            appUser.setFirstName(currentUser.getFirstName());
            appUser.setLastName(currentUser.getLastName());
            appUser.setPhoneNumber(currentUser.getPhoneNumber());

            // Get Budgets for user.
            List<Budget> budgets = budgetService.getBudgetsForUser(appUser.getUsername());

            Budget activeBudget = budgets.stream().filter((it) -> {
                return it.getActive().booleanValue();
            }).toList().get(0);

            List<Month> months = monthService.getMonthsByBudgetId(activeBudget.getId());
            months = MonthService.sortMonthsDesc(months);

            List<Transaction> transactions = transactionService.getTransactionsByMonthId(months.get(0).getId());

            // TODO: Finish logic for loading the dashboard data.

            Gson gson = new Gson();
            String responseStr = gson.toJson(appUser);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseStr);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public class AppUser {
        private Long userId;
        private String username;
        private String firstName;
        private String lastName;
        private String phoneNumber;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
