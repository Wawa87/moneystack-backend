package com.wawa87.moneystack.service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MarcoServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MarcoServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    String subject = JwtUtil.validateAndGetSubject(cookie.getValue());
                    response.getWriter().print("MarcoServlet: " + subject);
                }
            }
        }
    }
}
