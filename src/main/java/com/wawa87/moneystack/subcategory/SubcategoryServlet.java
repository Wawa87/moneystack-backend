package com.wawa87.moneystack.subcategory;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.subcategory.model.Subcategory;
import com.wawa87.moneystack.subcategory.service.SubcategoryService;
import com.wawa87.moneystack.user.servlet.UserServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SubcategoryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    AppContext ctx;
    SubcategoryService subcategoryService;

    public SubcategoryServlet(AppContext ctx) {
        this.ctx = ctx;
        this.subcategoryService = ctx.getSubcategoryService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /subcategories/byCategoryId/{id}
        if (pathInfo.length == 3 && pathInfo[1].equals("byCategoryId")) {
            try {
                Long categoryId = Long.valueOf(pathInfo[2]);
                List<Subcategory> subcategories = subcategoryService.findByCategoryId(currentUserId, categoryId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, subcategories);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }

        // Handle request: /subcategories/{id}
        if (pathInfo.length == 2) {
            try {
                Long subcategoryId = Long.valueOf(pathInfo[1]);
                Subcategory subcategory = subcategoryService.findById(currentUserId, subcategoryId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, subcategory);
                return;
            } catch (ValidationException e) {
                ServletUtility.sendValidationException(response, e);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /subcategories
        // Create the Subcategory.
        try {
            Subcategory subcategory = ServletUtility.gson.fromJson(request.getReader(), Subcategory.class);
            subcategory = this.subcategoryService.save(currentUserId, subcategory);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_CREATED, subcategory);
            return;
        } catch (IOException e) {
            ServletUtility.sendInternalError(response, e);
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
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /subcategories/{id}
        // Update the Subcategory.
        try {
            Long subcategoryId = Long.valueOf(pathInfo[1]);
            Subcategory subcategoryUpdate = ServletUtility.gson.fromJson(request.getReader(), Subcategory.class);
            subcategoryUpdate = subcategoryService.update(currentUserId, subcategoryId, subcategoryUpdate);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, subcategoryUpdate);
            return;
        } catch (IOException e) {
            ServletUtility.sendInternalError(response, e);
            return;
        } catch (ValidationException e) {
            ServletUtility.sendValidationException(response, e);
            return;
        } catch (NotFoundException e) {
            ServletUtility.sendNotFoundException(response, e);
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
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /subcategories/{id}
        try {
            Long subcategoryId = Long.valueOf(pathInfo[1]);
            subcategoryService.delete(currentUserId, subcategoryId);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "Subcategory deleted.");
            return;
        } catch (ValidationException e) {
            ServletUtility.sendValidationException(response, e);
            return;
        } catch (NotFoundException e) {
            ServletUtility.sendNotFoundException(response, e);
            return;
        } catch (BadRequestException e) {
            ServletUtility.sendBadRequest(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }
}
