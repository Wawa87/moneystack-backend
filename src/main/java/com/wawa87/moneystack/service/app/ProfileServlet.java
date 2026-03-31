package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.auth.JwtUtil;
import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.models.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ProfileServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProfileServlet.class);
    UserService userService;

    public ProfileServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Optional<User> userRes = this.userService.getUser((String) request.getAttribute("subject"));
        if (userRes.isPresent()) {
            User currentUser = userRes.get();
            ProfileServlet.UserProfile userProfile = new ProfileServlet.UserProfile();
            userProfile.setUserId(currentUser.getUserId());
            userProfile.setEmails(currentUser.getEmails());
            userProfile.setFirstName(currentUser.getFirstName());
            userProfile.setLastName(currentUser.getLastName());
            userProfile.setPhoneNumber(currentUser.getPhoneNumber());

            Gson gson = new Gson();
            String responseStr = gson.toJson(userProfile);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                response.getWriter().write(responseStr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<User> userRes = userService.getUser((String) request.getAttribute("subject"));
        if (userRes.isPresent()) {
            User user = userRes.get();

            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = request.getReader()) {
                bufferedReader.lines().forEach( line -> { stringBuilder.append(line); });
            }

            Gson gson = new Gson();
            ProfileServlet.UserProfile userProfile = gson.fromJson(stringBuilder.toString(), ProfileServlet.UserProfile.class);

            user.setEmails(userProfile.getEmails());
            user.setFirstName(userProfile.getFirstName());
            user.setLastName(userProfile.getLastName());
            user.setPhoneNumber(userProfile.getPhoneNumber());

            int result = userService.updateUser(user);
            response.getWriter().write(result);
            return;
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    public class UserProfile {
        private String userId;
        private ArrayList<String> emails;
        private String firstName;
        private String lastName;
        private String phoneNumber;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public ArrayList<String> getEmails() {
            return emails;
        }

        public void setEmails(ArrayList<String> emails) {
            this.emails = emails;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
