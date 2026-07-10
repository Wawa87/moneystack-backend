package com.wawa87.moneystack.service.app;

import com.wawa87.moneystack.service.auth.JwtUtil;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.model.UserResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuthenticationFilter extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private UserService userService;

    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
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
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    String subject = JwtUtil.validateAndGetSubject(cookie.getValue());
                    UserResponse userResponse = this.userService.findUserByUsername(subject);

                    // Valid subject is in the token. Add User attributes.
                    if (userResponse != null) {
                        request.setAttribute("subject", subject);
                        request.setAttribute("userId", userResponse.getId());
                        filterChain.doFilter(request, response);
                        return;
                    }
                }
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }
}
