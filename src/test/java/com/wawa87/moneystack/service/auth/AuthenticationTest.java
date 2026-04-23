package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.user.model.User;
import de.mkammerer.argon2.Argon2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class AuthenticationTest {
    @Test
    public void testRegistration() throws Exception {
        String json = "{" +
                "\"userId\": \"testUser\"," +
                "\"email\": \"testUser@email.com\"," +
                "\"firstName\": \"Test\"," +
                "\"lastName\": \"User\"," +
                "\"password\": \"FirstTestPass\"," +
                "\"phoneNumber\": \"+17602220101\"" +
                "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/register"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(response.body(), "Successfully registered user: testUser");

        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);

            Optional<User> userRes = userDAO.findByUsername("testUser");
            if (userRes.isPresent()) {
                User user = userRes.get();
                userDAO.delete(user);
            }

            connection.commit();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLogin() throws SQLException {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            Argon2 argon2 = Argon2Util.getArgon2();
            UserService userService = new UserService(userDAO, argon2);

            User user = userService.register(
                    "testUser",
                    "testUser@email.com",
                    "Test",
                    "User",
                    "testpass",
                    "17602221111"
            );

            String json = "{" +
                    "\"username\": \"testUser\"," +
                    "\"password\": \"testpass\"" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(200, response.statusCode());

            String subject = JwtUtil.validateAndGetSubject(response.body());
            Assertions.assertEquals("testUser", subject);

            int result = userService.deleteUser(user);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
