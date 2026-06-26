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

import java.io.BufferedReader;
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
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));
        String subject = request.getAttribute("subject").toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Process: /category/all/<username>
        if (pathInfo.matches("^/all/[a-zA-z0-9]+")) {
            String[] split = pathInfo.split("/");
            String username = split[2];

            // Disallow actions on categories for other users.
            if (!subject.equals(username)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Authenticated user mismatch: " + username + "\"}");
                return;
            }

            List<CategoryDTO> categories = categoryService.getCategories(username);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(this.gson.toJson(categories));
            return;
        }

        // Process: /category/<categoryId>
        if (pathInfo.matches("/^[0-9]+")) {
            String[] split = pathInfo.split("/");
            Long categoryId = Long.parseLong(split[1]);

            CategoryDTO category = categoryService.getCategoryDTOById(categoryId);

            if (category.getId() != userId) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Authenticated user mismatch for categoryId: " + categoryId + "\"}");
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(this.gson.toJson(category));
            return;
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));
        String subject = request.getAttribute("subject").toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Process: /category/new
        if (pathInfo.matches("/new")) {
            try {
                CategoryData categoryData = parseCategory(request);
                Category newCategory =  new Category();
                newCategory.setUserId(userId);
                newCategory.setName(categoryData.getName());
                newCategory.setDescription(categoryData.getDescription());

                newCategory = categoryService.saveCategory(newCategory);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(this.gson.toJson(newCategory));
                return;
            } catch (IOException e) {
                logger.error(e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        // Process: /category/update/<categoryId>
        if (pathInfo.matches("/update/^[0-9]+")) {
            String[] split = pathInfo.split("/");
            Long categoryId = Long.parseLong(split[2]);

            try {
                CategoryData categoryData = parseCategory(request);

                CategoryDTO categoryDTO = categoryService.getCategoryDTOById(categoryId);

                if (categoryDTO.getUserId() != userId) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Authenticated user mismatch for categoryId: " + categoryId + "\"}");
                    return;
                }

                Category updatedCategory = new Category();
                updatedCategory.setId(categoryDTO.getId());
                updatedCategory.setUserId(categoryDTO.getUserId());
                updatedCategory.setName(categoryData.getName());
                updatedCategory.setDescription(categoryData.getDescription());
                updatedCategory = categoryService.saveCategory(updatedCategory);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(this.gson.toJson(updatedCategory));
                return;
            } catch (IOException e) {
                logger.error(e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

    private CategoryData parseCategory(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = request.getReader()) {
            bufferedReader.lines().forEach(line -> { stringBuilder.append(line);});
            CategoryData categoryData = this.gson.fromJson(stringBuilder.toString(), CategoryData.class);
            return categoryData;
        }
    }

    private class CategoryData {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
