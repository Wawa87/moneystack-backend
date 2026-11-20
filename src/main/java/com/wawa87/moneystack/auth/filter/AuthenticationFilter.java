package com.wawa87.moneystack.auth.filter;

import com.auth0.jwt.interfaces.Claim;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.auth.util.JwtUtil;
import com.wawa87.moneystack.common.util.ServletUtility;
import com.wawa87.moneystack.user.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class AuthenticationFilter extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private AppContext ctx;
    private JwtUtil jwtUtil;
    private UserService userService;

    public AuthenticationFilter(AppContext ctx) {
        this.ctx = ctx;
        this.jwtUtil = ctx.getJwtUtil();
        this.userService = ctx.getUserService();
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        ArrayList<String> authBypassPaths = new ArrayList<>();
        authBypassPaths.add("/auth");
//        authBypassPaths.add("/register");
//        authBypassPaths.add("/login");
//        authBypassPaths.add("/validateNewUsername");
//        authBypassPaths.add("/validateNewUsername/");
//        authBypassPaths.add("/forgot-password");
//        authBypassPaths.add("/reset-password");

        if (authBypassPaths.contains(servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // Get the access_token cookie.
            Optional<Cookie> tokenOpt = Arrays.stream(cookies).filter((it) -> {
                return it.getName().equals("access_token");
            }).findFirst();

            // Return bad request if cookie is missing.
            if (tokenOpt.isEmpty()) {
                ServletUtility.sendAuthorizationException(response);
                return;
            } else {
                Map<String, Claim> claims = this.jwtUtil.validateAndGetClaims(tokenOpt.get().getValue());

                // Return bad request if there is no validated claims.
                if (claims == null || !claims.containsKey("userId") || claims.get("userId").isMissing()) {
                    ServletUtility.sendAuthorizationException(response);
                    return;
                } else {
                    request.setAttribute("currentUserId", claims.get("userId").asString());
                    request.setAttribute("currentUsername", claims.get("username").asString());

                    // Continue filter chain for valid token.
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        ServletUtility.sendAuthorizationException(response);
        return;
    }
}
