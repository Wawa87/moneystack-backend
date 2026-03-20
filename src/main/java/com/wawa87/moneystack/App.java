package com.wawa87.moneystack;

import com.wawa87.moneystack.service.auth.Argon2Util;
import com.wawa87.moneystack.service.auth.AuthenticationServlet;
import com.wawa87.moneystack.service.auth.MarcoServlet;
import com.wawa87.moneystack.service.auth.RegistrationServlet;
import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.dao.UserDAOImpl;
import com.wawa87.moneystack.service.users.db.PGUtil;
import de.mkammerer.argon2.Argon2;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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

//        FilterHolder dispatcherFilter = new FilterHolder(new DispatcherFilter());
//        context.addFilter(
//                dispatcherFilter,
//                "/*",
//                EnumSet.of(DispatcherType.REQUEST)
//        );

        ServletHolder authenticationServlet = new ServletHolder(new AuthenticationServlet(userService));
        context.addServlet(authenticationServlet, "/login");

        ServletHolder registrationServlet = new ServletHolder(new RegistrationServlet(userService));
        context.addServlet(registrationServlet, "/register");

        ServletHolder marcoServlet = new ServletHolder(new MarcoServlet());
        context.addServlet(marcoServlet, "/marco");

        server.start();
        server.join();
    }
}