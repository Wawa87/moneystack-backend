package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.service.auth.service.AuthorizationService;
import com.wawa87.moneystack.service.auth.service.AuthorizationServiceImpl;
import com.wawa87.moneystack.service.auth.util.Argon2Util;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAO;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAO;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.service.system.month.dao.MonthDAO;
import com.wawa87.moneystack.service.system.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAOImpl;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAOImpl;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import de.mkammerer.argon2.Argon2;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;

public class AuthenticationTest {
    private DataSource dataSource;
    private Argon2 argon2;
    private AuthorizationService authorizationService;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private SubcategoryDAO subcategoryDAO;
    private MonthDAO monthDAO;
    private BudgetDAO budgetDAO;
    private TransactionDAO transactionDAO;

    @BeforeEach
    public void initializeTest() {
        this.argon2 = Argon2Util.getArgon2();
        this.dataSource = PGUtil.getDataSource();
        this.userDAO = new UserDAOImpl(this.dataSource);
        this.categoryDAO = new CategoryDAOImpl(this.dataSource);
        this.subcategoryDAO = new SubcategoryDAOImpl(this.dataSource);
        this.monthDAO = new MonthDAOImpl(this.dataSource);
        this.budgetDAO = new BudgetDAOImpl(this.dataSource);
        this.transactionDAO = new TransactionDAOImpl(this.dataSource);
        this.authorizationService = new AuthorizationServiceImpl(this.userDAO, this.categoryDAO, this.subcategoryDAO, this.budgetDAO, this.monthDAO, this.transactionDAO);
    }

//    @Test
//    public void testRegistration() throws Exception {
//        String json = "{" +
//                "\"userId\": \"testUser\"," +
//                "\"email\": \"testUser@email.com\"," +
//                "\"firstName\": \"Test\"," +
//                "\"lastName\": \"User\"," +
//                "\"password\": \"FirstTestPass\"," +
//                "\"phoneNumber\": \"+17602220101\"" +
//                "}";
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//            .uri(URI.create("http://localhost:8080/register"))
//                .header("User-Agent", "Java Test App")
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//        Assertions.assertEquals(response.statusCode(), 201);
//        Assertions.assertEquals(response.body(), "Successfully registered user: testUser");
//
//        try {
//            UserDAOImpl userDAO = new UserDAOImpl(this.dataSource);
//
//            Optional<User> userRes = userDAO.findByUsername("testUser");
//            if (userRes.isPresent()) {
//                User user = userRes.get();
//                userDAO.delete(user);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testLogin() throws SQLException {
//        try {
//            UserService userService = new UserService(this.userDAO, this.argon2, authorizationService);
//
//            UserRequest userRequest = new UserRequest();
//            userRequest.setUsername("testUser");
//            userRequest.setEmails(new ArrayList<>(List.of("testUser@email.com")));
//            userRequest.setFirstName("Test");
//            userRequest.setLastName("User");
//            userRequest.setPassword("testpass");
//            userRequest.setPhoneNumber("17602221111");
//
//            userService.register(userRequest);
//
//            String json = "{" +
//                    "\"username\": \"testUser\"," +
//                    "\"password\": \"testpass\"" +
//                    "}";
//
//            HttpClient client = HttpClient.newHttpClient();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:8080/login"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(json))
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            Assertions.assertEquals(200, response.statusCode());
//
//            String subject = JwtUtil.validateAndGetSubject(response.body());
//            Assertions.assertEquals("testUser", subject);
//
//            int result = userService.deleteUser(UserRequest.convertToUser(userRequest));
//            System.out.println(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}