package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.models.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class DeletionServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private UserService userService;

    public DeletionServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<User> userRes = userService.getUser((String) request.getAttribute("subject"));
        if (userRes.isPresent()) {
            User user = userRes.get();

            int result = userService.deleteUser(user);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("User deleted: " + user.getUserId());
            return;
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("User deletion failed.");
    }
}
