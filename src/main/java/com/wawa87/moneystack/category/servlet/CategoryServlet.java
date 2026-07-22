package com.wawa87.moneystack.category.servlet;

import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.category.service.CategoryService;
import com.wawa87.moneystack.category.dao.CategoryDTO;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.db.ResultStatus;
import com.wawa87.moneystack.common.db.ServletUtility;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class CategoryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServlet.class);
    AppContext ctx;
    CategoryService categoryService;

    public CategoryServlet(AppContext ctx) {
        this.ctx = ctx;
        this.categoryService = this.ctx.getCategoryService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        // Handle request: /categories
        if (pathInfo.length == 0) {
            List<Category> categories = categoryService.getCategoriesByUserId(userId);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, categories);
            return;
        }

        // Handle request: /categories/{id}
        if (pathInfo.length == 2) {
            Long id = Long.valueOf(pathInfo[1]);

            try {
                Category category = categoryService.findCategoryById(id, userId);
                if (category == null) category = new Category();
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, category);
                return;
            } catch (IllegalAccessException e) {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden.");
                return;
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /categories
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        try {
            CategoryDTO categoryDTO = ServletUtility.gson.fromJson(request.getReader(), CategoryDTO.class);

            // Validate the object.
            if (categoryDTO == null || categoryDTO.getName() == null || categoryDTO.getName().isBlank()) {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Category name is required.");
                return;
            }

            // Save the category.
            Category category = categoryService.saveCategory(userId, categoryDTO);

            if (category == null) {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error. Category not created.");
            }
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_CREATED, category);
            return;
        } catch (Exception e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /categories/{id}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        if (pathInfo.length != 2) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        }

        try {
            // Get the Category Id from the request path.
            Long categoryId = Long.valueOf(pathInfo[1]);

            // Read payload into object.
            CategoryDTO categoryDTO = ServletUtility.gson.fromJson(request.getReader(), CategoryDTO.class);

            // Validate the object.
            if (categoryDTO == null || categoryDTO.getName() == null || categoryDTO.getName().isBlank()) {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Category name is required.");
                return;
            }

            // Update the category.
            ResultStatus result = categoryService.updateCategory(categoryId, userId, categoryDTO);

            switch (result) {
                case SUCCESS: ServletUtility.sendResponse(response, HttpServletResponse.SC_NO_CONTENT, ""); break;
                case FORBIDDEN: ServletUtility.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden."); break;
                case NOT_FOUND: ServletUtility.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "Category not found."); break;
                default: ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            }
            return;
        } catch (JsonSyntaxException e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON.");
            return;
        } catch (NumberFormatException e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid category id.");
            return;
        } catch (Exception e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /categories/{categoryId}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        if (pathInfo.length != 2) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        }

        try {
            // Get the Category Id from the request path.
            Long categoryId = Long.valueOf(pathInfo[1]);

            // Delete the category.
            ResultStatus result = categoryService.deleteCategoryById(categoryId, userId);

            switch (result) {
                case SUCCESS: ServletUtility.sendResponse(response, HttpServletResponse.SC_NO_CONTENT, ""); break;
                case FORBIDDEN: ServletUtility.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden."); break;
                case NOT_FOUND: ServletUtility.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "Category not found."); break;
                default: ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            }
            return;
        } catch (NumberFormatException e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid category id.");
            return;
        } catch (Exception e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }

    private CategoryDTO parseCategory(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = request.getReader()) {
            bufferedReader.lines().forEach(line -> { stringBuilder.append(line);});
            CategoryDTO categoryData = ServletUtility.gson.fromJson(stringBuilder.toString(), CategoryDTO.class);
            return categoryData;
        }
    }
}
