package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wawa87.moneystack.service.system.category.model.Category;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.jsontype.impl.AsExistingPropertyTypeSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CategoryServletTest {
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
    public void testPost() throws IOException, InterruptedException {
        // Create Category.
        String json = "{\"name\": \"Entertainment\", \"description\": \"Entertainment expenses\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Category category = gson.fromJson(response.body(), Category.class);

        Assertions.assertTrue(category.getId() > 0);
    }

    @Test
    public void testGetAll() throws IOException, InterruptedException {
        // Create multiple Categories to retrieve.
        // Create Category.
        String json = "{\"name\": \"Entertainment\", \"description\": \"Entertainment expenses\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = "{\"name\": \"Housing\", \"description\": \"Housing expenses\"}";
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Get the categories list.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Category> categories = gson.fromJson(response.body(), new TypeToken<List<Category>>() {}.getType());

        Assertions.assertEquals(2, categories.size());
    }

    @Test
    public void testGetCategory() throws IOException, InterruptedException {
        // Create Category.
        String json = "{\"name\": \"Entertainment\", \"description\": \"Entertainment expenses\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Category category = gson.fromJson(response.body(), Category.class);

        // Get the Category.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Category fetchedCategory = gson.fromJson(response.body(), Category.class);

        Assertions.assertEquals(category.getId(), fetchedCategory.getId());
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        // Create Category.
        String json = "{\"name\": \"Entertainment\", \"description\": \"Entertainment expenses\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Category category = gson.fromJson(response.body(), Category.class);

        // Update the Category.
        category.setName("New Name");
        category.setDescription("New Description");
        json = "{" +
                "\"id\": \"" + category.getId() + "\"," +
                "\"userId\": \"" + category.getUserId() + "\"," +
                "\"name\": \"" + category.getName() + "\"," +
                "\"description\": \"" + category.getDescription() + "\"" +
                "}";

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Get the Category and confirm update.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Category updatedCategory = gson.fromJson(response.body(), Category.class);

        Assertions.assertEquals(category.getName(), updatedCategory.getName());
        Assertions.assertEquals(category.getDescription(), updatedCategory.getDescription());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        // Create Category.
        String json = "{\"name\": \"Entertainment\", \"description\": \"Entertainment expenses\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Category category = gson.fromJson(response.body(), Category.class);

        Assertions.assertTrue(category.getId() > 0); // Confirm that Category was created.

        // Get the Category.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Category fetchedCategory = gson.fromJson(response.body(), Category.class);

        // Delete the Category.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Get the list of Categories - should be empty.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Category> categories = gson.fromJson(response.body(), new TypeToken<List<Category>>() {}.getType());

        Assertions.assertEquals(0, categories.size());
    }
}