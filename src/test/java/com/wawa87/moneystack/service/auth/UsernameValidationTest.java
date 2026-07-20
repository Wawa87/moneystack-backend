package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.wawa87.moneystack.common.db.ResponseMessage;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.user.dao.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UsernameValidationTest {
    private String token;
    private UserDTO userDTO;

    private static Gson gson = new Gson();

    @BeforeEach
    public void prepareTest() throws IOException, InterruptedException {
        // Register the test user.
        String json = "{\"username\": \"dev\"," +
                "\"emails\": [\"dev@tester.com\"]," +
                "\"firstName\": \"App\"," +
                "\"lastName\": \"Dev\"," +
                "\"password\": \"dev\"," +
                "\"phoneNumber\": \"17602225555\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Login
        json = "{\"username\": \"dev\", \"password\": \"dev\"}";
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<String> cookies = response.headers().allValues("Set-Cookie");
        for (String s : cookies) {
            String[] splits = s.split("access_token=");
            if (splits.length > 1) {
                token = splits[1];
            }
        }
        userDTO = gson.fromJson(response.body(), UserDTO.class);
    }

    @AfterEach
    public void closeTest() throws IOException, InterruptedException {
        // Delete the test user.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/" + userDTO.getId()))
                .header("Content-Type", "application/json")
                .header("Cookie", "access_token=" + token)
                .DELETE()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testUsernameValidation() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String json = "";

        // Test empty username
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/validateUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .header("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.statusCode());

        // Test bad-format username
        json = "{\"username\": \"d e v\"}";

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/validateUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .header("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.statusCode());

        // Test username success.
        json = "{\"username\": \"dEv\"}";

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/validateUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .header("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ResponseMessage message = ServletUtility.gson.fromJson(response.body(), ResponseMessage.class);

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.statusCode());
        Assertions.assertEquals("true", message.getMessage());
    }

    private class ValidationBoolean {
        private String result;
    }
}
