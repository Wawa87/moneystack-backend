package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.system.category.CategoryService;
import com.wawa87.moneystack.service.system.category.dao.CategoryDTO;
import com.wawa87.moneystack.service.system.category.model.Category;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CategoryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServlet.class);
    CategoryService categoryService;
    Gson gson;

    public CategoryServlet(CategoryService categoryService) {
        this.categoryService = categoryService;
        this.gson = new Gson();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String subject = request.getAttribute("subject").toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Process category get: /category/username/getAll
        if (pathInfo.matches("^/[a-zA-z0-9]+/getAll")) {
            String[] split = pathInfo.split("/");
            String userId = split[1];

            // Disallow actions on categories for other users.
            if (!subject.equals(userId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Authenticated user mismatch: " + userId + "\"}");
                return;
            }

            List<CategoryDTO> categories = categoryService.getCategories(userId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(this.gson.toJson(categories));
            return;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

    }
}
