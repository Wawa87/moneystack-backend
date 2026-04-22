package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

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
        UserCredentials userCredentials = gson.fromJson(stringBuilder.toString(), UserCredentials.class);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (this.userService.authenticate(userCredentials.username, userCredentials.password)) {
            String token = JwtUtil.generateToken(userCredentials.username);

            String cookieStr = "access_token=" + token + "; SameSite=None; Secure; HttpOnly; Path=/; Max-Age=900";
            response.setHeader("Set-Cookie", cookieStr);
            response.setContentType("application/json");

            Optional<UserDTO> userRes = userService.getUserDTO(userCredentials.username);
            if (userRes.isPresent()) {
                UserDTO userDTO = userRes.get();

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(userDTO, UserDTO.class));
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed for username: " + userCredentials.username);
            return;
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Bad URI or user not found.");
        return;
    }

    private class UserCredentials {
        private String username;
        private String password;
    }
}
