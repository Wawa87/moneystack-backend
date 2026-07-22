package com.wawa87.moneystack.auth.servlet;

import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.model.UsernameValidationRequest;
import com.wawa87.moneystack.auth.service.AuthenticationService;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.user.service.UserService;
import com.wawa87.moneystack.user.servlet.RegistrationServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsernameValidationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private AppContext ctx;
    private UserService userService;
    private AuthenticationService authenticationService;

    public UsernameValidationServlet(AppContext ctx) {
        this.ctx = ctx;
        this.userService = this.ctx.getUserService();
        this.authenticationService = ctx.getAuthenticationService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /validateNewUsername
        try {
            UsernameValidationRequest usernameValidationRequest = ServletUtility.gson.fromJson(request.getReader(), UsernameValidationRequest.class);
            this.authenticationService.validateNewUsername(usernameValidationRequest.getUsername());
            ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "Username is available.");
        } catch (InvalidUsernameException e) {
            ServletUtility.sendInvalidUsernameException(response, e);
            return;
        } catch (JsonSyntaxException e) {
            ServletUtility.sendInternalError(response, e);
            return;
        } catch (NumberFormatException e) {
            ServletUtility.sendBadRequest(response);
            return;
        } catch (Exception e) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }
}
