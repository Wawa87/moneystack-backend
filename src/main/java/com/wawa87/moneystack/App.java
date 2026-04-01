package com.wawa87.moneystack;

import com.wawa87.moneystack.service.app.AuthenticationFilter;
import com.wawa87.moneystack.service.app.HomeServlet;
import com.wawa87.moneystack.service.app.ProfileServlet;
import com.wawa87.moneystack.service.auth.*;
import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.dao.UserDAOImpl;
import com.wawa87.moneystack.service.users.db.PGUtil;
import de.mkammerer.argon2.Argon2;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Properties;

public class App {
    public static DataSource dataSource = PGUtil.getDataSource();
    public static Argon2 argon2 = Argon2Util.getArgon2();
    public static UserService userService;
    public static Loader loader;
    public static Properties properties;

    static {
        try {
            Connection connection = dataSource.getConnection();

            UserDAOImpl userDAO = new UserDAOImpl(connection);
            userService = new UserService(userDAO, argon2);

            loader = new Loader();
            properties = loader.loadPropertiesFile("application.properties");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

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

        ServletHolder dashboardServlet = new ServletHolder(new HomeServlet(userService));
        context.addServlet(dashboardServlet, "/");

        ServletHolder profileServlet = new ServletHolder(new ProfileServlet(userService));
        context.addServlet(profileServlet, "/profile");

        server.start();
        server.join();
    }
}