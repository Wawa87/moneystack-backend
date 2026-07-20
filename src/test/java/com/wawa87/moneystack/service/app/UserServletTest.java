package com.wawa87.moneystack.service.app;

import com.google.gson.reflect.TypeToken;
import com.wawa87.moneystack.auth.model.AuthenticationRequest;
import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.auth.service.AuthorizationServiceImpl;
import com.wawa87.moneystack.budget.dao.BudgetDAO;
import com.wawa87.moneystack.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.common.db.PGUtil;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.month.dao.MonthDAO;
import com.wawa87.moneystack.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAOImpl;
import com.wawa87.moneystack.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.transaction.dao.TransactionDAOImpl;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.dao.UserDAOImpl;
import com.wawa87.moneystack.user.model.User;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServletTest {
    private UserResponse userResponse;
    private String token;
    private DataSource dataSource;
    private AuthorizationService authorizationService;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private SubcategoryDAO subcategoryDAO;
    private MonthDAO monthDAO;
    private BudgetDAO budgetDAO;
    private TransactionDAO transactionDAO;

    @BeforeEach
    public void prepareTest() throws Exception {
        // Initialize resources.
        this.dataSource = PGUtil.getDataSource();
        this.userDAO = new UserDAOImpl(this.dataSource);
        this.categoryDAO = new CategoryDAOImpl(this.dataSource);
        this.subcategoryDAO = new SubcategoryDAOImpl(this.dataSource);
        this.monthDAO = new MonthDAOImpl(this.dataSource);
        this.budgetDAO = new BudgetDAOImpl(this.dataSource);
        this.transactionDAO = new TransactionDAOImpl(this.dataSource);
        this.authorizationService = new AuthorizationServiceImpl(this.userDAO, this.categoryDAO, this.subcategoryDAO, this.budgetDAO, this.monthDAO, this.transactionDAO);

        // Create dev user.
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("dev");
        userRequest.setFirstName("App");
        userRequest.setLastName("Dev");
        userRequest.setEmails(new ArrayList<>(List.of("dev@tester.com", "dev2@tester.com")));
        userRequest.setPhoneNumber("17602225555");
        userRequest.setPassword("dev");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(userRequest)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Authenticate the dev user.
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(userRequest.getUsername());
        authenticationRequest.setPassword(userRequest.getPassword());
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(authenticationRequest)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        this.userResponse = ServletUtility.gson.fromJson(response.body(), UserResponse.class);

        List<String> cookies = response.headers().allValues("set-cookie");
        String tokenCookie = cookies.stream().filter((it) -> {
            return it.toString().contains("access_token");
        }).findFirst().get();

        this.token = tokenCookie.split("=")[1];
    }

    @AfterEach
    public void closeTest() {
        // Delete the test user.
        this.userDAO.delete(UserResponse.convertResponseToUser(this.userResponse));
    }

    @Test
    public void testGetAll() throws Exception {
        // Create multiple Users to retrieve.
        // Create Users.
        User user0 = new User();
        user0.setUsername("ckramer");
        user0.setPassword("yoyoma");
        user0.setEmails(new ArrayList<>(List.of("ckramer@seinfeld.com")));
        user0.setFirstName("Cosmo");
        user0.setLastName("Kramer");
        user0.setPhoneNumber("17602221111");

        user0 = this.userDAO.save(user0).get();

        User user1 = new User();
        user1.setUsername("gcostanza");
        user1.setPassword("bosco");
        user1.setEmails(new ArrayList<>(List.of("gcostanza@seinfeld.com")));
        user1.setFirstName("George");
        user1.setLastName("Costanza");
        user1.setPhoneNumber("17602222222");

        user1 = this.userDAO.save(user1).get();

        // Get Users.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users/all"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + this.token)
                .headers("Cookie", "currentUserId=" + this.userResponse.getId())
                .headers("Cookie", "currentUsername=" + this.userResponse.getUsername())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<UserResponse> users = ServletUtility.gson.fromJson(response.body(), new TypeToken<List<UserResponse>>(){}.getType());

        // Verify Users are retrieved.
        Assertions.assertEquals(3, users.size()); // Size is 3 including Dev user.

        // Clean up test users.
        userDAO.delete(user0);
        userDAO.delete(user1);
    }

    @Test
    public void testGetUser() throws Exception {
        // Create User to retrieve.
        // Create User.
        User user0 = new User();
        user0.setUsername("ckramer");
        user0.setPassword("yoyoma");
        user0.setEmails(new ArrayList<>(List.of("ckramer@seinfeld.com")));
        user0.setFirstName("Cosmo");
        user0.setLastName("Kramer");
        user0.setPhoneNumber("17602221111");

        user0 = this.userDAO.save(user0).get();

        // Get User.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users/" + user0.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + this.token)
                .headers("Cookie", "currentUserId=" + this.userResponse.getId())
                .headers("Cookie", "currentUsername=" + this.userResponse.getUsername())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        UserResponse retrievedUser = ServletUtility.gson.fromJson(response.body(), UserResponse.class);

        // Verify User
        Assertions.assertEquals(user0.getId(), retrievedUser.getId());
        Assertions.assertEquals(user0.getUsername(), retrievedUser.getUsername());

        // Clean up test user.
        userDAO.delete(user0);
    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        // Create User.
        UserRequest user0 = new UserRequest();
        user0.setUsername("ckramer");
        user0.setPassword("yoyoma");
        user0.setEmails(new ArrayList<>(List.of("ckramer@seinfeld.com")));
        user0.setFirstName("Cosmo");
        user0.setLastName("Kramer");
        user0.setPhoneNumber("17602221111");

        // Get User.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users"))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + this.token)
                .headers("Cookie", "currentUserId=" + this.userResponse.getId())
                .headers("Cookie", "currentUsername=" + this.userResponse.getUsername())
                .POST(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(user0)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        UserResponse retrievedUser = ServletUtility.gson.fromJson(response.body(), UserResponse.class);

        Assertions.assertTrue(0 < retrievedUser.getId());

        // Clean up test user.
        this.userDAO.delete(UserResponse.convertResponseToUser(retrievedUser));
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        // Create User.
        User user0 = new User();
        user0.setUsername("ckramer");
        user0.setPassword("yoyoma");
        user0.setEmails(new ArrayList<>(List.of("ckramer@seinfeld.com")));
        user0.setFirstName("Cosmo");
        user0.setLastName("Kramer");
        user0.setPhoneNumber("17602221111");

        user0 = this.userDAO.save(user0).get();

        Assertions.assertTrue(0 < user0.getId());

        User user0Updated = new User();
        user0Updated.setId(user0.getId());
        user0Updated.setUsername("kman");
        user0Updated.setPassword("CottonDockers!");
        user0Updated.setEmails(new ArrayList<>(List.of("kman@seinfeld.com")));
        user0Updated.setFirstName("Cosmonaught");
        user0Updated.setLastName("Kramerica");
        user0Updated.setPhoneNumber("17602221112");

        // Update User.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users/" + user0Updated.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + this.token)
                .headers("Cookie", "currentUserId=" + this.userResponse.getId())
                .headers("Cookie", "currentUsername=" + this.userResponse.getUsername())
                .PUT(HttpRequest.BodyPublishers.ofString(ServletUtility.gson.toJson(user0Updated)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        UserResponse userResponse0 = ServletUtility.gson.fromJson(response.body(), UserResponse.class);

        Assertions.assertEquals(user0Updated.getId(), userResponse0.getId());
        Assertions.assertEquals(user0Updated.getUsername(), userResponse0.getUsername());
        Assertions.assertEquals(user0Updated.getEmails().get(0), userResponse0.getEmails().get(0));
        Assertions.assertEquals(user0Updated.getFirstName(), userResponse0.getFirstName());
        Assertions.assertEquals(user0Updated.getLastName(), userResponse0.getLastName());
        Assertions.assertEquals(user0Updated.getPhoneNumber(), userResponse0.getPhoneNumber());

        // Clean up test User.
        this.userDAO.deleteById(user0Updated.getId());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        // Create User.
        User user0 = new User();
        user0.setUsername("ckramer");
        user0.setPassword("yoyoma");
        user0.setEmails(new ArrayList<>(List.of("ckramer@seinfeld.com")));
        user0.setFirstName("Cosmo");
        user0.setLastName("Kramer");
        user0.setPhoneNumber("17602221111");

        user0 = this.userDAO.save(user0).get();

        Assertions.assertTrue(0 < user0.getId());

        // Delete User.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users/" + user0.getId()))
                .header("Content-Type", "application/json")
                .headers("Cookie", "access_token=" + this.token)
                .headers("Cookie", "currentUserId=" + this.userResponse.getId())
                .headers("Cookie", "currentUsername=" + this.userResponse.getUsername())
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.statusCode());

        Optional<User> user0Opt = this.userDAO.findById(user0.getId());

        Assertions.assertTrue(user0Opt.isEmpty());
    }
}