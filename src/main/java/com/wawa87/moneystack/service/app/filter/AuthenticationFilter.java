package com.wawa87.moneystack.service.app.filter;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.service.auth.util.JwtUtil;
import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.user.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
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
        if (servletPath.equals("/register") || servletPath.equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // Get the currentUserId cookie.
            Optional<Cookie> userIdOpt = Arrays.stream(cookies).filter((it) -> {
                return it.getName().equals("currentUserId");
            }).findFirst();
            if (userIdOpt.isPresent()) request.setAttribute("currentUserId", userIdOpt.get().getValue());

            // Get the currentUsername cookie.
            Optional<Cookie> usernameOpt = Arrays.stream(cookies).filter((it) -> {
                return it.getName().equals("currentUsername");
            }).findFirst();
            if (usernameOpt.isPresent()) request.setAttribute("currentUsername", usernameOpt.get().getValue());

            // Get the access_token cookie.
            Optional<Cookie> tokenOpt = Arrays.stream(cookies).filter((it) -> {
                return it.getName().equals("access_token");
            }).findFirst();

            // Return bad request if cookie is missing.
            if (tokenOpt.isEmpty()) {
                ServletUtility.sendUnauthorized(response);
                return;
            } else {
                String subject = this.jwtUtil.validateAndGetSubject(tokenOpt.get().getValue());
                // Return bad request if there is no validated subject.
                if (subject.isBlank()) {
                    ServletUtility.sendUnauthorized(response);
                    return;
                } else {
                    // Continue filter chain for valid token.
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        ServletUtility.sendUnauthorized(response);
        return;
    }
}
