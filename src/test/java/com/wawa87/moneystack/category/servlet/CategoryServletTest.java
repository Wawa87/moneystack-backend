package com.wawa87.moneystack.category.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.model.AuthenticationRequest;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.User;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import com.wawa87.moneystack.user.service.UserService;
import org.checkerframework.checker.units.qual.C;
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

import static com.wawa87.moneystack.common.db.ServletUtility.gson;

public class CategoryServletTest {
    private static AppContext ctx;
    private static UserResponse userResponse0;
    private static UserResponse userResponse1;

    @BeforeAll
    public static void prepareAll() throws BadRequestException, InvalidUsernameException {
        ctx = new AppContext();
        UserService userService = ctx.getUserService();

        User user = new User();
        user.setUsername("dev");
        user.setFirstName("App");
        user.setLastName("Dev");
        user.setEmails(new ArrayList<>(List.of("dev@tester.com", "dev2@tester.com")));
        user.setPhoneNumber("17602225555");
        user.setPassword("dev");

        userResponse0 = userService.register(UserRequest.convertUsertoRequest(user));

        User user1 = new User();
        user1.setUsername("ckramer");
        user1.setPassword("yoyoma");
        user1.setEmails(new ArrayList<>(List.of("ckramer@seinfeld.com")));
        user1.setFirstName("Cosmo");
        user1.setLastName("Kramer");
        user1.setPhoneNumber("17602221111");

        userResponse1 = userService.register(UserRequest.convertUsertoRequest(user1));
    }

    @AfterAll
    public static void cleanAll() {
        UserDAO userDAO = ctx.getUserDAO();

        userDAO.deleteById(userResponse0.getId());
        userDAO.deleteById(userResponse1.getId());
    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        // Authenticate the user.
        AuthenticationRequest ar = new AuthenticationRequest();
        ar.setUsername("dev");
        ar.setPassword("dev");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, List<String>> headers = response.headers().map();
        List<String> cookies = headers.get("set-cookie");
        String cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token = cookie.split("=")[1];

        // Create Category.
        Category category = new Category();
        category.setUserId(userResponse0.getId());
        category.setName("Entertainment");
        category.setDescription("Voluntary entertainment expenses.");

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token)
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(category)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        category = gson.fromJson(response.body(), Category.class);

        Assertions.assertTrue(category.getId() > 0);
    }

    @Test
    public void testGetAll() throws IOException, InterruptedException {
        // Create multiple Categories to retrieve.
        // Create Category.
        Category category0 = new Category();
        category0.setUserId(userResponse0.getId());
        category0.setName("Entertainment");
        category0.setDescription("Voluntary entertainment expenses.");

        Category category1 = new Category();
        category1.setUserId(userResponse0.getId());
        category1.setName("Housing");
        category1.setDescription("All home expenses");

        CategoryDAO categoryDAO = ctx.getCategoryDAO();
        category0 = categoryDAO.save(category0).get();
        category1 = categoryDAO.save(category1).get();

        // Authenticate User 0.
        AuthenticationRequest ar = new AuthenticationRequest();
        ar.setUsername("dev");
        ar.setPassword("dev");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, List<String>> headers = response.headers().map();
        List<String> cookies = headers.get("set-cookie");
        String cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token0 = cookie.split("=")[1];

        // Get the Categories.
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token0)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Category> categories = ServletUtility.gson.fromJson(response.body(), new TypeToken<List<Category>>() {}.getType());

        // Test retrieved categories.
        Assertions.assertEquals(2, categories.size());

        // Authenticate User 1.
        ar = new AuthenticationRequest();
        ar.setUsername("ckramer");
        ar.setPassword("yoyoma");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        headers = response.headers().map();
        cookies = headers.get("set-cookie");
        cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token1 = cookie.split("=")[1];

        // Get the Categories.
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token1)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        categories = ServletUtility.gson.fromJson(response.body(), new TypeToken<List<Category>>() {}.getType());

        // Test categories are empty.
        Assertions.assertEquals(0, categories.size());
    }

    @Test
    public void testGetCategory() throws IOException, InterruptedException {
        // Create multiple Categories to retrieve.
        // Create Category.
        Category category0 = new Category();
        category0.setUserId(userResponse0.getId());
        category0.setName("Entertainment");
        category0.setDescription("Voluntary entertainment expenses.");

        Category category1 = new Category();
        category1.setUserId(userResponse0.getId());
        category1.setName("Housing");
        category1.setDescription("All home expenses");

        CategoryDAO categoryDAO = ctx.getCategoryDAO();
        category0 = categoryDAO.save(category0).get();
        category1 = categoryDAO.save(category1).get();

        // Authenticate User 0.
        AuthenticationRequest ar = new AuthenticationRequest();
        ar.setUsername("dev");
        ar.setPassword("dev");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, List<String>> headers = response.headers().map();
        List<String> cookies = headers.get("set-cookie");
        String cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token0 = cookie.split("=")[1];

        // Get a Category.
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category0.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token0)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Category retrievedcategory0 = ServletUtility.gson.fromJson(response.body(), Category.class);

        // Test retrieved category.
        Assertions.assertEquals(category0.getId(), retrievedcategory0.getId());

        // Authenticate User 1.
        ar = new AuthenticationRequest();
        ar.setUsername("ckramer");
        ar.setPassword("yoyoma");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        headers = response.headers().map();
        cookies = headers.get("set-cookie");
        cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token1 = cookie.split("=")[1];

        // Get the Categories.
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category0.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token1)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Test access denied to the category.
        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        // Create Category.
        Category category0 = new Category();
        category0.setUserId(userResponse0.getId());
        category0.setName("Entertainment");
        category0.setDescription("Voluntary entertainment expenses.");

        Category category1 = new Category();
        category1.setUserId(userResponse0.getId());
        category1.setName("Housing");
        category1.setDescription("All home expenses");

        CategoryDAO categoryDAO = ctx.getCategoryDAO();
        category0 = categoryDAO.save(category0).get();
        category1 = categoryDAO.save(category1).get();

        // Authenticate User 0.
        AuthenticationRequest ar = new AuthenticationRequest();
        ar.setUsername("dev");
        ar.setPassword("dev");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, List<String>> headers = response.headers().map();
        List<String> cookies = headers.get("set-cookie");
        String cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token0 = cookie.split("=")[1];

        // Update the Category.
        Category updateCategory0 = new Category();
        updateCategory0.setUserId(userResponse0.getId());
        updateCategory0.setName("Fun");
        updateCategory0.setDescription("All fun spends.");

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category0.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token0)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(updateCategory0)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Category updatedCategory0 = ServletUtility.gson.fromJson(response.body(), Category.class);

        // Test category was updated.
        Assertions.assertEquals(category0.getId(), updatedCategory0.getId());
        Assertions.assertEquals(category0.getUserId(), updatedCategory0.getUserId());
        Assertions.assertEquals(updateCategory0.getName(), updatedCategory0.getName());
        Assertions.assertEquals(updateCategory0.getDescription(), updatedCategory0.getDescription());

        // Authenticate User 1.
        ar = new AuthenticationRequest();
        ar.setUsername("ckramer");
        ar.setPassword("yoyoma");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        headers = response.headers().map();
        cookies = headers.get("set-cookie");
        cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token1 = cookie.split("=")[1];

        // Update Category 1.
        Category updateCategory1 = new Category();
        updateCategory1.setUserId(userResponse0.getId());
        updateCategory1.setName("Crash Pad");
        updateCategory1.setDescription("All shed expenses.");

        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category1.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token1)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(updateCategory1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Test access denied.
        Assertions.assertEquals(401, response.statusCode());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        // Create Category.
        Category category0 = new Category();
        category0.setUserId(userResponse0.getId());
        category0.setName("Entertainment");
        category0.setDescription("Voluntary entertainment expenses.");

        Category category1 = new Category();
        category1.setUserId(userResponse0.getId());
        category1.setName("Housing");
        category1.setDescription("All home expenses");

        CategoryDAO categoryDAO = ctx.getCategoryDAO();
        category0 = categoryDAO.save(category0).get();
        category1 = categoryDAO.save(category1).get();

        // Authenticate User 0.
        AuthenticationRequest ar = new AuthenticationRequest();
        ar.setUsername("dev");
        ar.setPassword("dev");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, List<String>> headers = response.headers().map();
        List<String> cookies = headers.get("set-cookie");
        String cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token0 = cookie.split("=")[1];

        // Delete the Category.
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category0.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token0)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Test success response.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(true, categoryDAO.findById(category0.getId()).isEmpty());

        // Authenticate User 1.
        ar = new AuthenticationRequest();
        ar.setUsername("ckramer");
        ar.setPassword("yoyoma");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ar)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        headers = response.headers().map();
        cookies = headers.get("set-cookie");
        cookie = cookies.stream().filter((it) -> { return it.startsWith("access_token"); }).findFirst().toString();
        String token1 = cookie.split("=")[1];

        // Delete Category 1.
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/categories/" + category1.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + token1)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Test access denied.
        Assertions.assertEquals(401, response.statusCode());
    }
}