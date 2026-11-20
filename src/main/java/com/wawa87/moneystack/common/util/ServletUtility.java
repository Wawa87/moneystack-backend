package com.wawa87.moneystack.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wawa87.moneystack.common.exceptions.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;

public class ServletUtility {
    private static final Logger logger = LoggerFactory.getLogger(ServletUtility.class);
    public static Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime .class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Month.class, new MonthAdapter())
            .registerTypeAdapter(Year.class, new YearAdapter())
            .create();

    /**
     * Send the HTTP response with an associated JSON message.
     * @param response
     * @param responseStatus
     * @param message
     */
    public static void sendResponse(HttpServletResponse response, int responseStatus, String message) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseStatus);
        if (!message.isEmpty()) {
            try {
                response.getWriter().write("{\"message\": \"" + message + "\"}");
            } catch (IOException e) {
                sendInternalError(response, e);
            }
        }
    }

    /**
     * Send the HTTP response with an associated JSON object.
     * @param response
     * @param responseStatus
     * @param responseObject
     * @param <T>
     */
    public static <T> void sendResponseObject(HttpServletResponse response, int responseStatus, Object responseObject) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseStatus);
        if (responseObject != null) {
            try {
                response.getWriter().write(gson.toJson(responseObject));
            } catch (IOException e) {
                sendInternalError(response, e);
            }
        }
    }

    /**
     * Send generic HTTP response with generic internal server error message.
     * @param response
     * @param e
     */
    public static void sendInternalError(HttpServletResponse response, Exception e) {
        sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
    }

    public static void sendBadRequest(HttpServletResponse response) {
        sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
    }

    public static void sendBadRequest(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    public static void sendBadRequest(HttpServletResponse response, ApiException e, String message) {
        sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, message);
    }

    public static void sendAuthorizationException(HttpServletResponse response) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized.");
    }

    public static void sendAuthorizationException(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    public static void sendAuthorizationException(HttpServletResponse response, ApiException e, String message) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    public static void sendValidationException(HttpServletResponse response) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Validation exception.");
    }

    public static void sendValidationException(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    public static void sendValidationException(HttpServletResponse response, ApiException e, String message) {
        sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    public static void sendNotFoundException(HttpServletResponse response) {
        sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "Not found.");
    }

    public static void sendNotFoundException(HttpServletResponse response, ApiException e) {
        sendResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    public static void sendNotFoundException(HttpServletResponse response, ApiException e, String message) {
        sendResponse(response, HttpServletResponse.SC_NOT_FOUND, message);
    }
}
