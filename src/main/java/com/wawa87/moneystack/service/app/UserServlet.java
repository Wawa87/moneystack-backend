package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.service.app.util.LocalDateTimeAdapter;
import com.wawa87.moneystack.service.auth.AuthorizationChecker;
import com.wawa87.moneystack.service.system.db.ResultStatus;
import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import com.wawa87.moneystack.service.system.user.dao.UserRequest;
import com.wawa87.moneystack.service.system.user.dao.UserResponse;
import com.wawa87.moneystack.service.system.user.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    UserService userService;
    Gson gson;

    public UserServlet(UserService userService) {
        this.userService = userService;
        this.gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        // Handle invalid request.
        if (pathInfo.length != 2) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request");
            return;
        }

        // Handle request: /users/all
        if (pathInfo.length == 2 && pathInfo[1].equals("all")) {
            List<User> users = userService.getUsers();
            List<UserResponse> usersResponse = new ArrayList<>();
            users.forEach((it) -> {
                UserResponse userResponse = UserResponse.convertUserToResponse(it);
                usersResponse.add(userResponse);
            });
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, usersResponse);
            return;
        }

        // Handle request: /users/{id}
        Long id = Long.valueOf(0);
        try {
            id = Long.valueOf(pathInfo[1]);
        } catch (NumberFormatException e) {
            logger.error("Error: " + e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad user id.");
            return;
        }

        if (pathInfo.length == 2 && Long.valueOf(pathInfo[1]) > 0) {
            UserResponse userResponse = userService.findUserById(id);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, userResponse);
            return;
        }

        ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /users
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        // Authorize endpoint for Admin only.
        if (!AuthorizationChecker.authorizeAdminUsername(request.getAttribute("subject").toString())) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Admin only.");
            return;
        }

        // Create the User.
        try {
            UserRequest userRequest = gson.fromJson(request.getReader(), UserRequest.class);
            UserResponse userResponse = userService.saveNewUser(userRequest);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_CREATED, userResponse);
            return;
        } catch (Exception e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        // Handle request: /user/{id}/updatePassword
        if (pathInfo.length == 3 && pathInfo[2].equals("updatePassword")) {
            try {
                // TODO: Implement password update workflow.
                ServletUtility.sendResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented.");
                return;
            } catch (Exception e) {
                // TODO: Implement proper exception handling.
                logger.error("Error: ", e);
                return;
            }
        }

        // Handle request: /user/{id}/updateEmails
        if (pathInfo.length == 3 && pathInfo[2].equals("updateEmails")) {
            try {
                // TODO: Implement emails update workflow.
                ServletUtility.sendResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented.");
                return;
            } catch (Exception e) {
                // TODO: Implement proper exception handling.
                logger.error("Error: ", e);
                return;
            }
        }

        // Handle request: /user/{id}
        if (pathInfo.length == 2) {
            try {
                // Get the User Id from the request path.
                Long id = Long.valueOf(pathInfo[1]);

                // Read the payload into object.
                User user = gson.fromJson(request.getReader(), User.class);

                // Update the User.
                ResultStatus result = userService.updateUser(user);

                switch (result) {
                    case SUCCESS: ServletUtility.sendResponse(response, HttpServletResponse.SC_NO_CONTENT, ""); break;
                    case FORBIDDEN: ServletUtility.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden."); break;
                    case NOT_FOUND: ServletUtility.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found."); break;
                    default: ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
                }
                return;
            } catch (JsonSyntaxException e) {
                logger.error("Error: ", e);
                ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON.");
                return;
            } catch (NumberFormatException e) {
                logger.error("Error: ", e);
                ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid User Id.");
                return;
            } catch (Exception e) {
                logger.error("Error: ", e);
                ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
                return;
            }
        }

        ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
        return;
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /user/{id}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        if (pathInfo.length != 2) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        }

        try {
            // Get the User Id from the request path.
            Long deleteUserId = Long.valueOf(pathInfo[1]);

            // Delete the User.
            ResultStatus result = userService.deleteUserById(deleteUserId, userId);

            switch (result) {
                case SUCCESS: ServletUtility.sendResponse(response, HttpServletResponse.SC_NO_CONTENT, ""); break;
                case FORBIDDEN: ServletUtility.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden."); break;
                case NOT_FOUND: ServletUtility.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found."); break;
                default: ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            }
            return;
        } catch (NumberFormatException e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user id.");
            return;
        } catch (Exception e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }
}
