package com.wawa87.moneystack.subcategory.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.model.AuthenticationRequest;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.util.TestData;
import com.wawa87.moneystack.subcategory.model.Subcategory;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.User;
import com.wawa87.moneystack.user.model.UserResponse;
import com.wawa87.moneystack.user.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class SubcategoryServletTest {
    private static AppContext ctx;
    private static UserService userService;
    private static UserDAO userDAO;
    private static CategoryDAO categoryDAO;

    @BeforeAll
    public static void prepareAll() throws BadRequestException, InvalidUsernameException {
        ctx = new AppContext();
        userService = ctx.getUserService();
        userDAO = ctx.getUserDAO();
        categoryDAO = ctx.getCategoryDAO();

        TestData.createTestUsers();
    }

    @AfterAll
    public static void cleanAll() {
        TestData.cleanTestUsers();
    }

    @Test
    public void testPost() throws NotFoundException, IOException, InterruptedException {
        // Get the User.
        UserResponse dev = userService.findUserByUsername("dev");
        AuthenticationRequest ar = new AuthenticationRequest();
        ar.setUsername(dev.getUsername());
        ar.setPassword("dev");
        String token;

        // Authenticate.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(ar)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, List<String>> headers = response.headers().map();
        List<String> cookies = headers.get("set-cookie");
        token = cookies.getFirst().split("=")[1];

        // Create a Category.
        Category category = new Category();
        category.setName("Housing");
        category.setDescription("All housing descriptions.");
        category.setUserId(dev.getId());

        category = categoryDAO.save(category).get();

        // Create the Subcategory.
        Subcategory subcategory = new Subcategory();
        subcategory.setCategoryId(category.getId());
        subcategory.setName("Mortgage");
        subcategory.setDescription("Recurring house payment");

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subcategories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(subcategory)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Test response.
        Assertions.assertEquals(201, response.statusCode());

        subcategory = ServletUtility.gson.fromJson(response.body(), Subcategory.class);

        Assertions.assertTrue(0 < subcategory.getId());
    }
}
