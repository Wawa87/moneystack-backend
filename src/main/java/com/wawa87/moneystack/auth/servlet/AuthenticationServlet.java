package com.wawa87.moneystack.auth.servlet;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.service.AuthenticationService;
import com.wawa87.moneystack.auth.util.JwtUtil;
import com.wawa87.moneystack.auth.model.AuthenticationRequest;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.user.service.UserService;
import com.wawa87.moneystack.user.model.UserResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuthenticationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);
    private AppContext ctx;
    private JwtUtil jwtUtil;
    private AuthenticationService authenticationService;
    private UserService userService;

    public AuthenticationServlet(AppContext ctx) {
        this.ctx = ctx;
        this.jwtUtil = ctx.getJwtUtil();
        this.authenticationService = ctx.getAuthenticationService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Handle request: /login
        try {
            AuthenticationRequest authenticationRequest = ServletUtility.gson.fromJson(request.getReader(), AuthenticationRequest.class);
            UserResponse userResponse = this.authenticationService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            String token = this.jwtUtil.generateToken(userResponse.getId() , authenticationRequest.getUsername());

            String cookieStr = "access_token=" + token + "; SameSite=None; Secure; HttpOnly; Path=/; Max-Age=900";
            response.setHeader("Set-Cookie", cookieStr);

            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, userResponse);
            return;
        } catch (JsonSyntaxException e) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        } catch (JsonIOException e) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        } catch (ValidationException e) {
            ServletUtility.sendValidationException(response, e);
            return;
        } catch (IOException e) {
            ServletUtility.sendInternalError(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
        }
    }
}
