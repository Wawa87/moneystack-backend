package com.wawa87.moneystack;

import com.wawa87.moneystack.service.auth.AuthenticationServlet;
import com.wawa87.moneystack.service.auth.DispatcherFilter;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;

public class App {
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

        ServletHolder authenticationServlet = new ServletHolder(new AuthenticationServlet());
        context.addServlet(authenticationServlet, "/authentication/login");

        server.start();
        server.join();
    }
}