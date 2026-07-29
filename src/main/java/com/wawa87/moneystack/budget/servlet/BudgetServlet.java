package com.wawa87.moneystack.budget.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.budget.model.Budget;
import com.wawa87.moneystack.budget.service.BudgetService;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.category.servlet.CategoryServlet;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.ServerError;
import java.util.List;

public class BudgetServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BudgetServlet.class);
    AppContext ctx;
    BudgetService budgetService;

    public BudgetServlet(AppContext ctx) {
        this.ctx = ctx;
        this.budgetService = ctx.getBudgetService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /budgets
        if (pathInfo.length == 0) {
            List<Budget> budgets = this.budgetService.getAll(currentUserId);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, budgets);
            return;
        }

        // Handle request: /budgets/{id}
        if (pathInfo.length == 2) {
            try {
                Long budgetId = Long.valueOf(pathInfo[1]);
                Budget budget = this.budgetService.findById(currentUserId,budgetId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, budget);
                return;
            } catch (ValidationException e) {
                ServletUtility.sendValidationException(response, e);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (AuthorizationException e) {
                ServletUtility.sendAuthorizationException(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /budgets
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        try {
            Budget budget = ServletUtility.gson.fromJson(request.getReader(), Budget.class);
            budget = this.budgetService.save(currentUserId, budget);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, budget);
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
        // Handle request: /budgets/{id}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        if (pathInfo.length != 2) {
            ServletUtility.sendBadRequest(response);
            return;
        }

        try {
            // Get the Budget Id from the request path.
            Long budgetId = Long.valueOf(pathInfo[1]);

            // Read payload into object.
            Budget budget = ServletUtility.gson.fromJson(request.getReader(), Budget.class);

            // Update the budget.
            budget = budgetService.update(currentUserId, budgetId, budget);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, budget);
            return;
        } catch (NotFoundException e) {
            ServletUtility.sendNotFoundException(response, e);
            return;
        } catch (BadRequestException e) {
            ServletUtility.sendBadRequest(response, e);
            return;
        } catch (IOException e) {
            ServletUtility.sendInternalError(response, e);
            return;
        } catch (AuthorizationException e) {
            ServletUtility.sendAuthorizationException(response, e);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /budgets/{id}
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        if (pathInfo.length != 2) {
            ServletUtility.sendBadRequest(response);
            return;
        }

        try {
            // Get the Budget Id from the request path.
            Long budgetId = Long.valueOf(pathInfo[1]);

            // Delete the category.
            budgetService.delete(currentUserId, budgetId);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "Budget deleted.");
            return;
        } catch (NumberFormatException e) {
            ServletUtility.sendBadRequest(response);
            return;
        } catch (AuthorizationException e) {
            ServletUtility.sendAuthorizationException(response, e);
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
