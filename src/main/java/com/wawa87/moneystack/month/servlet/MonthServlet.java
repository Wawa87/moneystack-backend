package com.wawa87.moneystack.month.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.common.util.ServletUtility;
import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.month.model.Month;
import com.wawa87.moneystack.month.service.MonthService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class MonthServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MonthServlet.class);
    AppContext ctx;
    MonthService monthService;

    public MonthServlet(AppContext ctx) {
        this.ctx = ctx;
        this.monthService = ctx.getMonthService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /months/{id}
        if (pathInfo.length == 2) {
            try {
                Long monthId = Long.valueOf(pathInfo[1]);
                Month month = this.monthService.findById(currentUserId, monthId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, month);
                return;
            } catch (AuthorizationException e) {
                ServletUtility.sendAuthorizationException(response, e);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            }
        }

        // Handle request: /months/ofBudget/{id}
        if (pathInfo.length == 3 && pathInfo[1].equals("ofBudget")) {
            try {
                Long budgetId = Long.valueOf(pathInfo[2]);
                List<Month> months = this.monthService.findByBudgetId(currentUserId, budgetId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, months);
                return;
            } catch (AuthorizationException e) {
                ServletUtility.sendAuthorizationException(response, e);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }

        ServletUtility.sendBadRequest(response);
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /months/forBudget/{id}
        if (pathInfo.length == 3 && pathInfo[1].equals("forBudget")) {
            try {
                Long budgetId = Long.valueOf(pathInfo[2]);
                Month month = ServletUtility.gson.fromJson(request.getReader(), Month.class);
                month.setBudgetId(budgetId);
                month = monthService.save(currentUserId, month);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, month);
                return;
            } catch (AuthorizationException e) {
                ServletUtility.sendAuthorizationException(response, e);
                return;
            } catch (NotFoundException e) {
                ServletUtility.sendNotFoundException(response, e);
                return;
            } catch (IOException e) {
                ServletUtility.sendInternalError(response, e);
                return;
            } catch (ValidationException e) {
                ServletUtility.sendValidationException(response, e);
                return;
            } catch (Exception e) {
                ServletUtility.sendInternalError(response, e);
                return;
            }
        }

        ServletUtility.sendBadRequest(response);
        return;
    }
}
