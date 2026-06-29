package com.wawa87.moneystack.service.system.db;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletUtility {
    private static Gson gson = new Gson();

    public static void sendResponse(HttpServletResponse response, int responseStatus, String message) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseStatus);
        if (!message.isEmpty()) {
            try {
                response.getWriter().write("{\"message\": \"" + message + "\"}");
            } catch (IOException e) {
                sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void sendResponseObject(HttpServletResponse response, int responseStatus, Object responseObject) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseStatus);
        if (responseObject != null) {
            try {
                response.getWriter().write(gson.toJson(responseObject));
            } catch (IOException e) {
                sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
                throw new RuntimeException(e);
            }
        }
    }
}
