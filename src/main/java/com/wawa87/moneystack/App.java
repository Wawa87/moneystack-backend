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
import com.wawa87.moneystack.service.system.transaction.TransactionService;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAOImpl;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import de.mkammerer.argon2.Argon2;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Properties;

public class App {
    public static DataSource dataSource = PGUtil.getDataSource();
    public static Argon2 argon2 = Argon2Util.getArgon2();
    public static UserService userService;
    public static BudgetService budgetService;
    public static CategoryService categoryService;
    public static MonthService monthService;
    public static TransactionService transactionService;
    public static Loader loader;
    public static Properties properties;

    static {
        try {
            Connection connection = dataSource.getConnection();

            UserDAO userDAO = new UserDAOImpl(connection);
            userService = new UserService(userDAO, argon2);

            BudgetDAO budgetDAO = new BudgetDAOImpl(connection);
            budgetService = new BudgetService(budgetDAO);

            CategoryDAO categoryDAO = new CategoryDAOImpl(connection);
            categoryService = new CategoryService(categoryDAO);

            MonthDAO monthDAO = new MonthDAOImpl(connection);
            monthService = new MonthService(monthDAO);

            TransactionDAO transactionDAO = new TransactionDAOImpl(connection);
            transactionService = new TransactionService(transactionDAO);

            loader = new Loader();
            properties = loader.loadPropertiesFile("application.properties");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
//        Server server = new Server(8080);
        Server server = new Server();

        // SSL Context Configuration
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath("src/main/resources/certs/jetty.keystore.jks");
        sslContextFactory.setKeyStorePassword("localdev");
        sslContextFactory.setKeyManagerPassword("localdev");

        // HTTPS Configuration for SSL
        HttpConfiguration httpsConfig = new HttpConfiguration();
        httpsConfig.setSecureScheme("https");
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Connection Factory
        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());

        // HTTP Connection Factory
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpsConfig);

        // Create and add the Connector
        ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
        sslConnector.setPort(8443);

        server.addConnector(sslConnector);

        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        FilterHolder preflightFilter = new FilterHolder(new PreflightFilter());
        context.addFilter(
                preflightFilter,
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        );

        FilterHolder authenticationFilter = new FilterHolder(new AuthenticationFilter());
        context.addFilter(
                authenticationFilter,
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        );

        ServletHolder authenticationServlet = new ServletHolder(new AuthenticationServlet(userService));
        context.addServlet(authenticationServlet, "/login");

        ServletHolder registrationServlet = new ServletHolder(new RegistrationServlet(userService));
        context.addServlet(registrationServlet, "/register");

        ServletHolder marcoServlet = new ServletHolder(new MarcoServlet());
        context.addServlet(marcoServlet, "/marco");

        ServletHolder homeServlet = new ServletHolder(new HomeServlet(userService, budgetService, monthService, transactionService));
        context.addServlet(homeServlet, "/");

        ServletHolder profileServlet = new ServletHolder(new UserServlet(userService));
        context.addServlet(profileServlet, "/user/*");

        ServletHolder usernameValidationServlet = new ServletHolder(new UsernameValidationServlet(userService));
        context.addServlet(usernameValidationServlet, "/validateUsername/*");

        ServletHolder categoryServlet = new ServletHolder(new CategoryServlet(categoryService));
        context.addServlet(categoryServlet, "/category/*");

        server.start();
        server.join();
    }
}