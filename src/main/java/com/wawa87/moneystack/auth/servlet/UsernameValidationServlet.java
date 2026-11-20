package com.wawa87.moneystack.auth.servlet;

import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.model.UsernameValidationRequest;
import com.wawa87.moneystack.auth.model.UsernameValidationResponse;
import com.wawa87.moneystack.auth.service.AuthenticationService;
import com.wawa87.moneystack.common.util.ServletUtility;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.user.service.UserService;
import com.wawa87.moneystack.user.servlet.RegistrationServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.ServerError;

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
            UsernameValidationResponse usernameValidationResponse = new UsernameValidationResponse();
            usernameValidationResponse.setResult(this.authenticationService.validateNewUsername(usernameValidationRequest.getUsername()));
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, usernameValidationResponse);
        } catch (InvalidUsernameException e) {
            ServletUtility.sendValidationException(response, e);
            return;
        } catch (JsonSyntaxException e) {
            ServletUtility.sendInternalError(response, e);
            return;
        } catch (NumberFormatException e) {
            ServletUtility.sendInternalError(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }
}
