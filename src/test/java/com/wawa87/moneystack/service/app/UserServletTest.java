package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import com.wawa87.moneystack.service.system.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserServletTest {
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
    public void testGetAll() throws IOException, InterruptedException {
        // Create multiple Users to retrieve.
        // Create User.
        String json = "{\"username\": \"ckramer\"," +
                "\"emails\": [\"ckramer@seinfeld.com\"]," +
                "\"firstName\": \"Cosmo\"," +
                "\"lastName\": \"Kramer\"," +
                "\"password\": \"yoyoma\"," +
                "\"phoneNumber\": \"17602221111\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User user0 = gson.fromJson(response.body(), User.class);

        // Create second User.
        json = "{\"username\": \"gcostanza\"," +
                "\"emails\": [\"gcostanza@seinfeld.com\"]," +
                "\"firstName\": \"George\"," +
                "\"lastName\": \"Costanza\"," +
                "\"password\": \"bosco\"," +
                "\"phoneNumber\": \"17602222222\"}";
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User user1 = gson.fromJson(response.body(), User.class);

        // Get Users.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/all"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<User> users = gson.fromJson(response.body(), new TypeToken<List<User>>() {}.getType());

        // Verify Users are retrieved.
        Assertions.assertEquals(2, users.size());
    }

    @Test
    public void testGetUser() throws IOException, InterruptedException {
        // Create User to retrieve.
        // Create User.
        String json = "{\"username\": \"ckramer\"," +
                "\"emails\": [\"ckramer@seinfeld.com\"]," +
                "\"firstName\": \"Cosmo\"," +
                "\"lastName\": \"Kramer\"," +
                "\"password\": \"yoyoma\"," +
                "\"phoneNumber\": \"17602221111\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User user = gson.fromJson(response.body(), User.class);

        // Get User.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/" + user.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        User retrievedUser = gson.fromJson(response.body(), User.class);

        // Verify User
        Assertions.assertEquals(user.getId(), retrievedUser.getId());
        Assertions.assertEquals(user.getUsername(), retrievedUser.getUsername());
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        // Create User to update.
        // Create User.
        String json = "{\"username\": \"ckramer\"," +
                "\"emails\": [\"ckramer@seinfeld.com\"]," +
                "\"firstName\": \"Cosmo\"," +
                "\"lastName\": \"Kramer\"," +
                "\"password\": \"yoyoma\"," +
                "\"phoneNumber\": \"17602221111\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User user = gson.fromJson(response.body(), User.class);

        // Update User.
        user.setUsername("kman");
        user.setFirstName("Bob");
        user.setLastName("Sacamano");
        user.setEmails(new ArrayList<>(Arrays.asList("bob@seinfeld.com")));
        json = "{\"username\": " + user.getUsername() + "," +
                "\"emails\": "+ user.getEmails() + "," +
                "\"firstName\": " + user.getFirstName() + "," +
                "\"lastName\": " + user.getLastName() + "," +
                "\"password\": \"yoyoma\"," +
                "\"phoneNumber\": \"17602221111\"}";
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/" + user.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Get the updated User.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/" + user.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User updatedUser = gson.fromJson(response.body(), User.class);

        // Verify updated User values.
        Assertions.assertEquals(user.getId(), updatedUser.getId());
        Assertions.assertEquals(user.getEmails(), updatedUser.getEmails());
        Assertions.assertEquals(user.getFirstName(), updatedUser.getFirstName());
        Assertions.assertEquals(user.getLastName(), updatedUser.getLastName());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        // Create User to delete.
        // Create User.
        String json = "{\"username\": \"ckramer\"," +
                "\"emails\": [\"ckramer@seinfeld.com\"]," +
                "\"firstName\": \"Cosmo\"," +
                "\"lastName\": \"Kramer\"," +
                "\"password\": \"yoyoma\"," +
                "\"phoneNumber\": \"17602221111\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User user = gson.fromJson(response.body(), User.class);

        Assertions.assertTrue(user.getId() > 0);

        // Delete user.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/" + user.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Get User.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/" + user.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        User retrievedUser = gson.fromJson(response.body(), User.class);

        Assertions.assertNull(retrievedUser);
    }
}
