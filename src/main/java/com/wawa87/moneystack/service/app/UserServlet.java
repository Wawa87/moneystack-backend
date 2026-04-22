package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import com.wawa87.moneystack.service.system.user.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    UserService userService;

    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Process profile get: /user/<userId>
        if (pathInfo.matches("^/[a-zA-z0-9]+/*")) {
            String[] split = pathInfo.split("/");
            String userId = split[1];

            Optional<UserDTO> userRes = userService.getUserDTO(userId);
            if (userRes.isPresent()) {
                UserDTO userDTO = userRes.get();

                Gson gson = new Gson();
                String responseBody = gson.toJson(userDTO, UserDTO.class);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(responseBody);
                return;
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Failed to retrieve user: " + userId);
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Bad URI.");
        return;
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Process profile update: /user/<userId>/update/profile
        if (pathInfo.matches("^/[a-zA-z0-9]+/update/profile/*")) {
            String[] split = pathInfo.split("/");
            String userId = split[1];

            StringBuilder stringBuilder = new StringBuilder();
            request.getReader().lines().forEach(line -> {
                stringBuilder.append(line);
            });

            Gson gson = new Gson();
            UserDTO userDTO = gson.fromJson(stringBuilder.toString(), UserDTO.class);

            Optional<User> userRes = userService.getUser(userId);
            if (userRes.isPresent()) {
                User user = userRes.get();

                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setPhoneNumber(userDTO.getPhoneNumber());

                userService.updateUser(user);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Failed to update user: " + userId + "\n");
            response.getWriter().write(stringBuilder.toString());
            return;
        }

        // TODO: Process emails update: /user/<userId>/update/emails
        if (pathInfo.matches("^/[a-zA-z0-9]+/update/emails/*")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Not yet implemented.");
            return;
        }

        // TODO: Process password update: /user/<userId>/update/password
        if (pathInfo.matches("^/[a-zA-z0-9]+/update/password/*")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Not yet implemented.");
            return;
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Bad URI.");
        return;
    }
}
