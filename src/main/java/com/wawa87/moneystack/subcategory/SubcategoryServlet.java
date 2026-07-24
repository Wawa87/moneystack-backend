package com.wawa87.moneystack.subcategory;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.subcategory.model.Subcategory;
import com.wawa87.moneystack.subcategory.service.SubcategoryService;
import com.wawa87.moneystack.user.servlet.UserServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /categories
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
}
