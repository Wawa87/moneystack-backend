package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.user.model.User;
import com.wawa87.moneystack.service.system.user.model.UserRequest;
import de.mkammerer.argon2.Argon2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthenticationTest {
    DataSource dataSource;
    Argon2 argon2;
    AuthorizationService authorizationService;
    UserDAO userDAO;

    @BeforeEach
    public void initializeTest() {
        this.argon2 = Argon2Util.getArgon2();
        this.dataSource = PGUtil.getDataSource();
        this.userDAO = new UserDAOImpl(this.dataSource);
        this.authorizationService = new AuthorizationServiceServiceImpl(this.userDAO);
    }

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

        try {
            UserDAOImpl userDAO = new UserDAOImpl(this.dataSource);

            Optional<User> userRes = userDAO.findByUsername("testUser");
            if (userRes.isPresent()) {
                User user = userRes.get();
                userDAO.delete(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLogin() throws SQLException {
        try {
            UserService userService = new UserService(this.userDAO, this.argon2, authorizationService);

            UserRequest userRequest = new UserRequest();
            userRequest.setUsername("testUser");
            userRequest.setEmails(new ArrayList<>(List.of("testUser@email.com")));
            userRequest.setFirstName("Test");
            userRequest.setLastName("User");
            userRequest.setPassword("testpass");
            userRequest.setPhoneNumber("17602221111");

            userService.register(userRequest);

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

            int result = userService.deleteUser(UserRequest.convertToUser(userRequest));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}