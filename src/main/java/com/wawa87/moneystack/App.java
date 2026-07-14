package com.wawa87.moneystack;

import com.wawa87.moneystack.service.app.*;
import com.wawa87.moneystack.service.auth.*;
import com.wawa87.moneystack.service.system.budget.BudgetService;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAO;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.service.system.category.CategoryService;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAO;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.service.system.month.MonthService;
import com.wawa87.moneystack.service.system.month.dao.MonthDAO;
import com.wawa87.moneystack.service.system.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.service.system.subcategory.SubcategoryService;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAOImpl;
import com.wawa87.moneystack.service.system.transaction.TransactionService;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAOImpl;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import de.mkammerer.argon2.Argon2;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.sql.DataSource;
import java.util.EnumSet;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws Exception {
        // Initizalize
        DataSource dataSource = PGUtil.getDataSource();
        Argon2 argon2 = Argon2Util.getArgon2();

        Loader loader = new Loader();
        Properties properties = loader.loadPropertiesFile("application.properties");
        JwtUtil jwtUtil = new JwtUtil(properties.getProperty("JWT_SECRET"), properties.getProperty("JWT_SECRET"));

        UserDAO userDAO = new UserDAOImpl(dataSource);
        BudgetDAO budgetDAO = new BudgetDAOImpl(dataSource);
        CategoryDAO categoryDAO = new CategoryDAOImpl(dataSource);
        SubcategoryDAO subcategoryDAO = new SubcategoryDAOImpl(dataSource);
        MonthDAO monthDAO = new MonthDAOImpl(dataSource);
        TransactionDAO transactionDAO = new TransactionDAOImpl(dataSource);

        AuthorizationServiceServiceImpl authorizationServiceImpl = new AuthorizationServiceServiceImpl(userDAO);
        UserService userService = new UserService(userDAO, argon2, authorizationServiceImpl);
        BudgetService budgetService = new BudgetService(budgetDAO);
        CategoryService categoryService = new CategoryService(categoryDAO);
        SubcategoryService subcategoryService = new SubcategoryService(subcategoryDAO);
        MonthService monthService = new MonthService(monthDAO);
        TransactionService transactionService = new TransactionService(transactionDAO, categoryService, subcategoryService);

        // Start server.
        Server server = new Server(8080);
//        Server server = new Server();

        // SSL Context Configuration
//        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
//        sslContextFactory.setKeyStorePath("src/main/resources/certs/jetty.keystore.jks");
//        sslContextFactory.setKeyStorePassword("localdev");
//        sslContextFactory.setKeyManagerPassword("localdev");

        // HTTPS Configuration for SSL
//        HttpConfiguration httpsConfig = new HttpConfiguration();
//        httpsConfig.setSecureScheme("https");
//        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Connection Factory
//        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());

        // HTTP Connection Factory
//        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpsConfig);

        // Create and add the Connector
//        ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
//        sslConnector.setPort(8443);

//        server.addConnector(sslConnector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

//        FilterHolder preflightFilter = new FilterHolder(new PreflightFilter());
//        context.addFilter(
//                preflightFilter,
//                "/*",
//                EnumSet.of(DispatcherType.REQUEST)
//        );

        FilterHolder authenticationFilter = new FilterHolder(new AuthenticationFilter(jwtUtil, userService));
        context.addFilter(
                authenticationFilter,
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        );

        ServletHolder authenticationServlet = new ServletHolder(new AuthenticationServlet(jwtUtil, userService));
        context.addServlet(authenticationServlet, "/login");

        ServletHolder registrationServlet = new ServletHolder(new RegistrationServlet(userService));
        context.addServlet(registrationServlet, "/register");

        ServletHolder profileServlet = new ServletHolder(new UserServlet(userService));
        context.addServlet(profileServlet, "/users/*");

        ServletHolder usernameValidationServlet = new ServletHolder(new UsernameValidationServlet(userService));
        context.addServlet(usernameValidationServlet, "/validateNewUsername");

        ServletHolder categoryServlet = new ServletHolder(new CategoryServlet(categoryService));
        context.addServlet(categoryServlet, "/categories/*");

        server.start();
        server.join();
    }
}