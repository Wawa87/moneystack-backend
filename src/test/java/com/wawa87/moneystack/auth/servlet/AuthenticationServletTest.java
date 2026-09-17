package com.wawa87.moneystack.auth.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.model.AuthenticationRequest;
import com.wawa87.moneystack.common.util.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.User;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import com.wawa87.moneystack.user.service.UserService;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthenticationServletTest {
    private static AppContext ctx;
    private static User user;

    @BeforeAll
    public static void prepareAll() throws BadRequestException, InvalidUsernameException {
        ctx = new AppContext();
        UserService userService = ctx.getUserService();

        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("dev");
        userRequest.setEmails(new ArrayList<>(List.of("dev@test.com")));
        userRequest.setFirstName("Dev");
        userRequest.setLastName("User");
        userRequest.setPhoneNumber("17602221111");
        userRequest.setPassword("dev");

        UserResponse userResponse = userService.register(userRequest);
        user = UserResponse.convertResponseToUser(userResponse);
    }

    @AfterAll
    public static void closeAll() {
        UserDAO userDAO = ctx.getUserDAO();
        userDAO.deleteById(user.getId());
    }

    @Test
    public void testAuthenticate() throws IOException, InterruptedException {
        AuthenticationRequest ar = new AuthenticationRequest();
        ar.setUsername(user.getUsername());
        ar.setPassword("dev");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(ar)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        UserResponse userResponse = ServletUtility.gson.fromJson(response.body(), UserResponse.class);

        Assertions.assertEquals(user.getId(), userResponse.getId());

        Map<String, List<String>> map = response.headers().map();
        List<String> setCookie = map.get("set-cookie");
        List<String> cookies = List.of(setCookie.get(0).split(";"));
        Optional<String> accessToken = cookies.stream().filter((it) -> {
            return it.startsWith("access_token");
        }).findFirst();
        String token = accessToken.get().split("=")[1];

        Assertions.assertTrue(!token.isBlank());
    }
}