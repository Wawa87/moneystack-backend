package com.wawa87.moneystack.service.app.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.service.auth.service.AuthorizationService;
import com.wawa87.moneystack.service.system.exceptions.*;
import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.model.User;
import com.wawa87.moneystack.service.system.user.model.UserRequest;
import com.wawa87.moneystack.service.system.user.model.UserResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    AppContext ctx;
    AuthorizationService authorizationService;
    UserService userService;

    public UserServlet(AppContext ctx) {
        this.ctx = ctx;
        this.authorizationService = this.ctx.getAuthorizationService();
        this.userService = this.ctx.getUserService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle invalid request.
        if (pathInfo.length != 2) {
            ServletUtility.sendBadRequest(response);
            return;
        }

        // Handle request: /users/all
        if (pathInfo.length == 2 && pathInfo[1].equals("all")) {
            try {
                List<UserResponse> usersResponse = userService.getUsers(currentUsername);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, usersResponse);
                return;
            } catch (ValidationException e) {
                ServletUtility.sendValidationException(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }

        // Handle request: /users/{id}
        if (pathInfo.length == 2 && !pathInfo[1].equals("all")) {
            try {
                Long requestedId = Long.valueOf(pathInfo[1]);
                UserResponse userResponse = userService.findUserById(currentUserId, requestedId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, userResponse);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (ValidationException e) {
                ServletUtility.sendValidationException(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }

        ServletUtility.sendBadRequest(response);
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /users
        // Create the User.
        try {
            UserRequest userRequest = ServletUtility.gson.fromJson(request.getReader(), UserRequest.class); // Read the payload into UserRequest object.
            UserResponse userResponse = this.userService.saveNewUser(userRequest, currentUsername);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_CREATED, userResponse);
            return;
        } catch (ValidationException e) {
            ServletUtility.sendValidationException(response, e);
            return;
        } catch (InvalidUsernameException e) {
            ServletUtility.sendInvalidUsernameException(response, e);
            return;
        } catch (BadRequestException e) {
            ServletUtility.sendBadRequest(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

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

        // Handle request: /user/{id}/updatePhoneNumber
        if (pathInfo.length == 3 && pathInfo[2].equals("updatePhoneNumber")) {
            try {
                // TODO: Implement phone number update workflow.
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
                User user = ServletUtility.gson.fromJson(request.getReader(), User.class);

                // Update the User.
                UserResponse userResponse = userService.updateUser(id, user, currentUsername);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, userResponse);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (BadRequestException e) {
                ServletUtility.sendBadRequest(response, e);
                return;
            } catch (ValidationException e) {
                ServletUtility.sendValidationException(response, e);
                return;
            } catch (IOException e) {
                ServletUtility.sendInternalError(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }

        ServletUtility.sendBadRequest(response);
        return;
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /user/{id}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        if (pathInfo.length != 2) {
            ServletUtility.sendBadRequest(response);
            return;
        }

        try {
            // Get the User Id from the request path.
            Long deleteUserId = Long.valueOf(pathInfo[1]);

            // Delete the User.
            this.userService.deleteUserById(deleteUserId, currentUserId);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_NO_CONTENT, "User deleted: " + deleteUserId);
            return;
        } catch (NumberFormatException e) {
            ServletUtility.sendBadRequest(response);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }
}
