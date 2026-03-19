package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.users.dao.UserDAOImpl;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;

public class AuthenticationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username =  request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("username = {}, password = {}", username, password);
        response.getWriter().println("AuthenticationServlet...");
        response.getWriter().println(username);
        response.getWriter().println(password);

        StringBuilder  stringBuilder = new StringBuilder();
        System.out.println("=================" + request.getContentType());

        try (BufferedReader bufferedReader = request.getReader()) {
            bufferedReader.lines().forEach(line -> { stringBuilder.append(line);});
        }

        System.out.println("=======================================" + stringBuilder.toString());

        Gson gson = new Gson();
        AuthObject authObject = gson.fromJson(stringBuilder.toString(), AuthObject.class);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println("Username:" + authObject.getUsername());
        response.getWriter().println("Password:" + authObject.getPassword());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username =  request.getParameter("username");
        String password = request.getParameter("password");

        if (isValid(username, password)) {
            String token = JwtUtil.generateToken(username);
            response.setContentType("application/json");
            response.getWriter().write("{\"token\":\""+token+"\"}");
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

     private boolean isValid(String username, String password) {
         return username.equals("testuser") && password.equals("testpass");
     }
}
