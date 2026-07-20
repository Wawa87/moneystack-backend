package com.wawa87.moneystack.common.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wawa87.moneystack.common.util.LocalDateTimeAdapter;
import com.wawa87.moneystack.common.exceptions.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class ServletUtility {
    public static Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime .class, new LocalDateTimeAdapter()).create();
    private static final Logger logger = LoggerFactory.getLogger(ServletUtility.class);

    public static void sendResponse(HttpServletResponse response, int responseStatus, String message) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseStatus);
        if (!message.isEmpty()) {
            try {
                response.getWriter().write("{\"message\": \"" + message + "\"}");
            } catch (IOException e) {
                sendInternalError(response, e);
//                sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
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
                sendInternalError(response, e);
//                sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
//                throw new RuntimeException(e);
            }
        }
    }

    public static void sendInternalError(HttpServletResponse response, Exception e) {
        logger.error("Error: " + e);
        sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
    }

    public static void sendBadRequest(HttpServletResponse response) {
        sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
    }

    public static void sendBadRequest(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    public static void sendUnauthorized(HttpServletResponse response) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized.");
    }

    public static void sendInvalidUsernameException(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    public static void sendValidationException(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    public static void sendNotFoundException(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }
}
