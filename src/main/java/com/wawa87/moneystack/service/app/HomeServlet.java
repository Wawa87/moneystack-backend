package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wawa87.moneystack.service.app.util.DashboardSet;
import com.wawa87.moneystack.service.app.util.LocalDateTimeAdapter;
import com.wawa87.moneystack.service.system.budget.BudgetService;
import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.month.MonthService;
import com.wawa87.moneystack.service.system.month.model.Month;
import com.wawa87.moneystack.service.system.transaction.TransactionService;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDTO;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import com.wawa87.moneystack.service.system.user.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
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
            UserDTO userDTO = new UserDTO();
            userDTO.setId(currentUser.getId());
            userDTO.setUsername(currentUser.getUsername());
            userDTO.setEmails(currentUser.getEmails());
            userDTO.setFirstName(currentUser.getFirstName());
            userDTO.setLastName(currentUser.getLastName());
            userDTO.setPhoneNumber(currentUser.getPhoneNumber());

            DashboardSet dashboardSet = new DashboardSet();
            dashboardSet.setUser(userDTO);

            // Get dashboard data for the user: Active budget and transactions.
            List<Budget> budgets = budgetService.getBudgetsForUser(userDTO.getUsername());

            if (budgets.size() > 0) {
                Budget activeBudget = budgets.stream().filter((it) -> {
                    return it.getActive().booleanValue();
                }).toList().get(0);

                dashboardSet.setActiveBudget(activeBudget);

                List<Month> months = monthService.getMonthsByBudgetId(activeBudget.getId());
                months = MonthService.sortMonthsDesc(months);

                if (months.size() > 0) {
                    List<TransactionDTO> transactionDTOs = transactionService.getTransactionDTOsByMonthId(months.get(0).getId());
                    dashboardSet.setTransactions(transactionDTOs);
                }
            }

            Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

            String responseStr = gson.toJson(dashboardSet);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseStr);

            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
