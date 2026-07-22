package com.wawa87.moneystack.auth.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.model.UsernameValidationRequest;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.user.model.User;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class UsernameValidationServletTest {
    private static AppContext ctx;
    private static User user;

    @BeforeAll
    public static void prepareAll() throws BadRequestException, InvalidUsernameException {
        ctx = new AppContext();

        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("dev");
        userRequest.setEmails(new ArrayList<>(List.of("dev@test.com")));
        userRequest.setFirstName("Dev");
        userRequest.setLastName("User");
        userRequest.setPhoneNumber("17602221111");
        userRequest.setPassword("dev");

        UserResponse userResponse = ctx.getUserService().register(userRequest);
        user = UserResponse.convertResponseToUser(userResponse);
    }

    @AfterAll
    public static void cleanAll() {
        ctx.getUserDAO().deleteById(user.getId());
    }

    @Test
    public void testUsernameValidation() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        UsernameValidationRequest usernameValidationRequest = new UsernameValidationRequest();
        usernameValidationRequest.setUsername("");

        // Test empty username
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/validateNewUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(usernameValidationRequest)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.statusCode());

        // Test bad-format username
        usernameValidationRequest.setUsername("d e v");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/validateNewUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(usernameValidationRequest)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.statusCode());

        // Test username taken case-insensitive.
        usernameValidationRequest.setUsername("dEv");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/validateNewUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(usernameValidationRequest)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.statusCode());

        // Test username success.
        usernameValidationRequest.setUsername("dev2");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/validateNewUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(usernameValidationRequest)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.statusCode());
    }
}
