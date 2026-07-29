package com.wawa87.moneystack.category.servlet;

import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.category.service.CategoryServiceImpl;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CategoryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServlet.class);
    AppContext ctx;
    CategoryServiceImpl categoryService;

    public CategoryServlet(AppContext ctx) {
        this.ctx = ctx;
        this.categoryService = this.ctx.getCategoryService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /categories
        if (pathInfo.length == 0) {
            List<Category> categories = categoryService.getAll(currentUserId);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, categories);
            return;
        }

        // Handle request: /categories/{id}
        if (pathInfo.length == 2) {
            Long id = Long.valueOf(pathInfo[1]);

            try {
                Category category = categoryService.findById(currentUserId, id);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, category);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (ValidationException e) {
                ServletUtility.sendValidationException(response, e);
                return;
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /categories
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        try {
            Category category = ServletUtility.gson.fromJson(request.getReader(), Category.class);

            // Save the category.
            category = categoryService.save(currentUserId, category);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_CREATED, category);
            return;
        } catch (BadRequestException e) {
            ServletUtility.sendBadRequest(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /categories/{id}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        if (pathInfo.length != 2) {
            ServletUtility.sendBadRequest(response);
            return;
        }

        try {
            // Get the Category Id from the request path.
            Long categoryId = Long.valueOf(pathInfo[1]);

            // Read payload into object.
            Category category = ServletUtility.gson.fromJson(request.getReader(), Category.class);

            // Update the category.
            category = categoryService.update(currentUserId, categoryId, category);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, category);
            return;
        } catch (JsonSyntaxException e) {
            ServletUtility.sendBadRequest(response);
            return;
        } catch (NumberFormatException e) {
            ServletUtility.sendBadRequest(response);
            return;
        } catch (NotFoundException e) {
            ServletUtility.sendNotFoundException(response, e);
            return;
        } catch (ValidationException e) {
            ServletUtility.sendValidationException(response, e);
            return;
        } catch (BadRequestException e) {
            ServletUtility.sendBadRequest(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /categories/{id}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        if (pathInfo.length != 2) {
            ServletUtility.sendBadRequest(response);
            return;
        }

        try {
            // Get the Category Id from the request path.
            Long categoryId = Long.valueOf(pathInfo[1]);

            // Delete the category.
            categoryService.delete(currentUserId, categoryId);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "Category deleted.");
            return;
        } catch (NumberFormatException e) {
            ServletUtility.sendBadRequest(response);
            return;
        } catch (ValidationException e) {
            ServletUtility.sendValidationException(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }
}
