package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wawa87.moneystack.service.app.util.DashboardSet;
import com.wawa87.moneystack.service.app.util.LocalDateTimeAdapter;
import com.wawa87.moneystack.service.auth.Argon2Util;
import com.wawa87.moneystack.service.system.budget.BudgetService;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAO;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.category.CategoryService;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAO;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.service.system.category.model.Category;
import com.wawa87.moneystack.service.system.month.MonthService;
import com.wawa87.moneystack.service.system.month.dao.MonthDAO;
import com.wawa87.moneystack.service.system.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.service.system.month.model.Month;
import com.wawa87.moneystack.service.system.subcategory.SubcategoryService;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAOImpl;
import com.wawa87.moneystack.service.system.subcategory.model.Subcategory;
import com.wawa87.moneystack.service.system.transaction.TransactionService;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAOImpl;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.user.model.User;
import de.mkammerer.argon2.Argon2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

public class HomeServletTest {
    @Test
    public void testHomeNoAuth() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void testHomeNewUser() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            Argon2 argon2 = Argon2Util.getArgon2();
            UserService userService = new UserService(userDAO, argon2);

            // Register the test user.
            User user = userService.register(
                    "cosmo",
                    "kman@seinfeld.com",
                    "Cosmo",
                    "Kramer",
                    "yoyoma",
                    "+17602220101"
            );

            String json = "{" +
                    "\"username\": \"cosmo\"," +
                    "\"password\": \"yoyoma\"" +
                    "}";

            // Authenticate with the new user to get the JWT.
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<String> cookies = response.headers().allValues("Set-Cookie");
            String token = "";
            for (String s : cookies) {
                String[] splits = s.split("access_token=");
                if (splits.length > 1) {
                    token = splits[1];
                }
            }

            Assertions.assertEquals(200, response.statusCode());

            // Request the homepage and verify reponse.
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/"))
                    .header("Cookie", "access_token=" + token)
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(200, response.statusCode());

            Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
            DashboardSet appUser = gson.fromJson(response.body(), DashboardSet.class);

            Assertions.assertEquals("cosmo", appUser.getUser().getUsername());
            Assertions.assertEquals("Cosmo", appUser.getUser().getFirstName());
            Assertions.assertEquals("Kramer", appUser.getUser().getLastName());
            Assertions.assertEquals("+17602220101", appUser.getUser().getPhoneNumber());
            Assertions.assertNull(appUser.getActiveBudget());
            Assertions.assertNull(appUser.getTransactions());

            userService.deleteUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHomeUserWithData() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAOImpl(connection);
            CategoryDAO categoryDAO = new CategoryDAOImpl(connection);
            SubcategoryDAO subcategoryDAO = new SubcategoryDAOImpl(connection);
            BudgetDAO budgetDAO = new BudgetDAOImpl(connection);
            MonthDAO monthDAO = new MonthDAOImpl(connection);
            TransactionDAO transactionDAO = new TransactionDAOImpl(connection);

            Argon2 argon2 = Argon2Util.getArgon2();
            UserService userService = new UserService(userDAO, argon2);
            CategoryService categoryService = new CategoryService(categoryDAO);
            SubcategoryService subcategoryService = new SubcategoryService(subcategoryDAO);
            BudgetService budgetService = new BudgetService(budgetDAO);
            MonthService monthService = new MonthService(monthDAO);
            TransactionService transactionService = new TransactionService(transactionDAO);

            // Register the test user.
            User user = userService.register(
                    "cosmo",
                    "kman@seinfeld.com",
                    "Cosmo",
                    "Kramer",
                    "yoyoma",
                    "+17602220101"
            );

            Category category = new Category();
            category.setName("Housing");
            category.setUserId(user.getId());
            category.setDescription("All housing expenses");
            categoryService.saveCategory(category);

            Category category1 = new Category();
            category1.setName("Entertainment");
            category1.setUserId(user.getId());
            category1.setDescription("Voluntary entertainment expenses");
            categoryService.saveCategory(category1);

            Subcategory subcategory = new Subcategory();
            subcategory.setName("Mortgage");
            subcategory.setDescription("House payment");
            subcategory.setCategoryId(category.getId());
            subcategoryService.saveSubcategory(subcategory);

            Subcategory subcategory1 = new Subcategory();
            subcategory1.setName("Paramount+");
            subcategory1.setDescription("Streaming service");
            subcategory1.setCategoryId(category1.getId());
            subcategoryService.saveSubcategory(subcategory1);

            Budget budget = new Budget();
            budget.setName("Big Spender");
            budget.setUserId(user.getId());
            budget.setActive(true);
            budgetService.saveBudget(budget);

            Month month = new Month();
            month.setBudgetId(budget.getId());
            month.setYear(Year.of(2026));
            month.setMonth(java.time.Month.APRIL);
            monthService.saveMonth(month);

            Transaction transaction0 = new Transaction();
            transaction0.setMonthId(month.getId());
            transaction0.setAmount(BigDecimal.valueOf(1800));
            transaction0.setCategoryId(category.getId());
            transaction0.setSubcategoryId(subcategory.getId());
            transaction0.setDescription("involuntary");
            transaction0.setTimestamp(LocalDateTime.now());
            transactionService.saveTransaction(transaction0);

            Transaction transaction1 = new Transaction();
            transaction1.setMonthId(month.getId());
            transaction1.setAmount(BigDecimal.valueOf(27.99));
            transaction1.setCategoryId(category1.getId());
            transaction1.setSubcategoryId(subcategory1.getId());
            transaction1.setDescription("uncontrolled spending");
            transaction1.setTimestamp(LocalDateTime.now());
            transactionService.saveTransaction(transaction1);

            String json = "{" +
                    "\"username\": \"cosmo\"," +
                    "\"password\": \"yoyoma\"" +
                    "}";

            // Authenticate with the new user to get the JWT.
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<String> cookies = response.headers().allValues("Set-Cookie");
            String token = "";
            for (String s : cookies) {
                String[] splits = s.split("access_token=");
                if (splits.length > 1) {
                    token = splits[1];
                }
            }

            Assertions.assertEquals(200, response.statusCode());

            // Request the homepage and verify reponse.
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/"))
                    .header("Cookie", "access_token=" + token)
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(200, response.statusCode());

            Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
            DashboardSet dashboardSet = gson.fromJson(response.body(), DashboardSet.class);

            Assertions.assertEquals("cosmo", dashboardSet.getUser().getUsername());
            Assertions.assertEquals("Cosmo", dashboardSet.getUser().getFirstName());
            Assertions.assertEquals("Kramer", dashboardSet.getUser().getLastName());
            Assertions.assertEquals("+17602220101", dashboardSet.getUser().getPhoneNumber());
            Assertions.assertEquals(budget.getName(), dashboardSet.getActiveBudget().getName());
            Assertions.assertEquals(transaction0.getId(), dashboardSet.getTransactions().get(0).getId());
            Assertions.assertEquals(transaction1.getId(), dashboardSet.getTransactions().get(1).getId());

            userService.deleteUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
