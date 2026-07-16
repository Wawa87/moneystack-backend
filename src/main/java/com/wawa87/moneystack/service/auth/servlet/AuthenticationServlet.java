package com.wawa87.moneystack.service.auth.servlet;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.service.auth.util.JwtUtil;
import com.wawa87.moneystack.service.auth.model.AuthenticationRequest;
import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.exceptions.ValidationException;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.model.UserResponse;
import jakarta.servlet.http.Cookie;
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
    private UserService userService;

    public AuthenticationServlet(AppContext ctx) {
        this.ctx = ctx;
        this.jwtUtil = ctx.getJwtUtil();
        this.userService = ctx.getUserService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Handle request: /login
        try {
            AuthenticationRequest authenticationRequest = ServletUtility.gson.fromJson(request.getReader(), AuthenticationRequest.class);
            UserResponse userResponse = this.userService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            String token = this.jwtUtil.generateToken(authenticationRequest.getUsername());

            String cookieStr = "access_token=" + token + "; SameSite=None; Secure; HttpOnly; Path=/; Max-Age=900";
            response.setHeader("Set-Cookie", cookieStr);

            Cookie currentUserId = new Cookie("currentUserId", userResponse.getId().toString());
            currentUserId.setMaxAge(900);
            currentUserId.setSecure(true);
            currentUserId.setHttpOnly(true);

            Cookie currentUsername = new Cookie("currentUsername", userResponse.getUsername());
            currentUsername.setMaxAge(900);
            currentUsername.setSecure(true);
            currentUsername.setHttpOnly(true);

            response.addCookie(currentUserId);
            response.addCookie(currentUsername);

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
