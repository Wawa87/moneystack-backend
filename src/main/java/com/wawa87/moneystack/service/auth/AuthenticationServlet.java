package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.users.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class AuthenticationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);
    private UserService userService;

    public AuthenticationServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = request.getReader()) {
            bufferedReader.lines().forEach(line -> { stringBuilder.append(line);});
        }

        Gson gson = new Gson();
        AuthObject authObject = gson.fromJson(stringBuilder.toString(), AuthObject.class);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (this.userService.authenticate(authObject.getUsername(), authObject.getPassword())) {
            String token = JwtUtil.generateToken(authObject.getUsername());

            Cookie authCookie = new Cookie("access_token", token);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(false);      // only over HTTPS
            authCookie.setPath("/");         // or narrower path
            authCookie.setMaxAge(15 * 60);   // 15 minutes
            response.addCookie(authCookie);

            response.setContentType("application/json");
            response.getWriter().write(token);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed for username: " + authObject.getUsername());
        }
    }

     private class AuthObject {
         private String username;
         private String password;
         public AuthObject(String username, String password) {
             this.username = username;
             this.password = password;
         }
         public String getUsername() {
             return username;
         }
         public void setUsername(String username) {
             this.username = username;
         }
         public String getPassword() {
             return password;
         }
         public void setPassword(String password) {
             this.password = password;
         }
     }
}
