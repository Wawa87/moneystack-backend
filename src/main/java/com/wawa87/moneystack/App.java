package com.wawa87.moneystack;

import com.wawa87.moneystack.auth.filter.AuthenticationFilter;
import com.wawa87.moneystack.category.servlet.CategoryServlet;
import com.wawa87.moneystack.user.servlet.UserServlet;
import com.wawa87.moneystack.auth.servlet.AuthenticationServlet;
import com.wawa87.moneystack.auth.servlet.RegistrationServlet;
import com.wawa87.moneystack.auth.servlet.UsernameValidationServlet;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;

public class App {
    private static AppContext ctx;

    public static void main(String[] args) throws Exception {
        // Initizalize
        ctx = new AppContext();
//        DataSource dataSource = PGUtil.getDataSource();
//        Argon2 argon2 = Argon2Util.getArgon2();
//
//        Loader loader = new Loader();
//        Properties properties = loader.loadPropertiesFile("application.properties");
//        JwtUtil jwtUtil = new JwtUtil(properties.getProperty("JWT_SECRET"), properties.getProperty("JWT_SECRET"));
//
//        UserDAO userDAO = new UserDAOImpl(dataSource);
//        BudgetDAO budgetDAO = new BudgetDAOImpl(dataSource);
//        CategoryDAO categoryDAO = new CategoryDAOImpl(dataSource);
//        SubcategoryDAO subcategoryDAO = new SubcategoryDAOImpl(dataSource);
//        MonthDAO monthDAO = new MonthDAOImpl(dataSource);
//        TransactionDAO transactionDAO = new TransactionDAOImpl(dataSource);
//
//        AuthorizationServiceImpl authorizationServiceImpl = new AuthorizationServiceImpl(userDAO, categoryDAO, subcategoryDAO, budgetDAO, monthDAO, transactionDAO);
//        UserService userService = new UserService(userDAO, argon2, authorizationServiceImpl);
//        BudgetService budgetService = new BudgetService(budgetDAO);
//        CategoryService categoryService = new CategoryService(categoryDAO);
//        SubcategoryService subcategoryService = new SubcategoryService(subcategoryDAO);
//        MonthService monthService = new MonthService(monthDAO);
//        TransactionService transactionService = new TransactionService(transactionDAO, categoryService, subcategoryService);

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

        FilterHolder authenticationFilter = new FilterHolder(new AuthenticationFilter(ctx));
        context.addFilter(
                authenticationFilter,
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        );

        ServletHolder authenticationServlet = new ServletHolder(new AuthenticationServlet(ctx));
        context.addServlet(authenticationServlet, "/login");

        ServletHolder registrationServlet = new ServletHolder(new RegistrationServlet(ctx));
        context.addServlet(registrationServlet, "/register");

        ServletHolder profileServlet = new ServletHolder(new UserServlet(ctx));
        context.addServlet(profileServlet, "/users/*");

        ServletHolder usernameValidationServlet = new ServletHolder(new UsernameValidationServlet(ctx));
        context.addServlet(usernameValidationServlet, "/validateNewUsername");

        ServletHolder categoryServlet = new ServletHolder(new CategoryServlet(ctx));
        context.addServlet(categoryServlet, "/categories/*");

        server.start();
        server.join();
    }
}