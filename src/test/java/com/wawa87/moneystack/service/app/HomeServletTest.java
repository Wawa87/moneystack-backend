package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wawa87.moneystack.service.auth.Argon2Util;
import com.wawa87.moneystack.service.auth.JwtUtil;
import com.wawa87.moneystack.service.auth.RegistrationServlet;
import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.dao.UserDAOImpl;
import com.wawa87.moneystack.service.users.db.PGUtil;
import com.wawa87.moneystack.service.users.models.User;
import de.mkammerer.argon2.Argon2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;

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
    public void testHome() {
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

            Assertions.assertEquals(200, response.statusCode());

            // Request the homepage and verify reponse.
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/"))
                    .header("Cookie", "access_token=" + response.body())
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(200, response.statusCode());

            Gson gson = new Gson();
            HomeServlet.AppUser appUser = gson.fromJson(response.body(), HomeServlet.AppUser.class);

            Assertions.assertEquals("cosmo", appUser.getUserId());
            Assertions.assertEquals("Cosmo", appUser.getFirstName());
            Assertions.assertEquals("Kramer", appUser.getLastName());
            Assertions.assertEquals("+17602220101", appUser.getPhoneNumber());

            userService.deleteUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
