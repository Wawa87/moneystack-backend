package com.wawa87.moneystack.auth.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RegistrationServletTest {
    private static AppContext ctx;

    @BeforeAll
    public static void prepareTests() {
        ctx = new AppContext();
    }

    @Test
    public void testRegister() throws IOException, InterruptedException {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("dev");
        userRequest.setEmails(new ArrayList<>(List.of("dev@test.com")));
        userRequest.setFirstName("Dev");
        userRequest.setLastName("User");
        userRequest.setPassword("dev");
        userRequest.setPhoneNumber("17602221111");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(userRequest)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        UserResponse userResponse = ServletUtility.gson.fromJson(response.body(), UserResponse.class);

        Assertions.assertTrue(0 < userResponse.getId());

        UserDAO userDAO = ctx.getUserDAO();
        userDAO.deleteById(userResponse.getId());
    }
}
