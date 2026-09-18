package com.wawa87.moneystack.transaction.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.util.ServletUtility;
import com.wawa87.moneystack.transaction.model.Transaction;
import com.wawa87.moneystack.transaction.service.TransactionService;
import com.wawa87.moneystack.transaction.service.TransactionServiceImpl;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

public class TransactionServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServlet.class);
    AppContext ctx;
    TransactionService transactionService;

    public TransactionServlet(AppContext ctx) {
        this.ctx = ctx;
        this.transactionService = ctx.getTransactionService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /transactions/{id}
        if (pathInfo.length == 2) {
            try {
                Long transactionId = Long.valueOf(pathInfo[1]);
                Transaction transaction = transactionService.findById(currentUserId, transactionId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, transaction);
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

        // Handle request: /transactions/byMonth/{monthId}
        if (pathInfo.length == 3 && pathInfo[1].equals("byMonth")) {
            try {
                Long monthId = Long.valueOf(pathInfo[2]);
                List<Transaction> transactions = this.transactionService.getAllByMonth(currentUserId, monthId);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, transactions);
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

        // Handle request: /transactions
        if (pathInfo.length == 0) {
            try {
                Transaction transaction = ServletUtility.gson.fromJson(request.getReader(), Transaction.class);
                transactionService.save(currentUserId, transaction);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, transaction);
                return;
            } catch (IOException e) {
                ServletUtility.sendInternalError(response, e);
                return;
            } catch (AuthorizationException e) {
                ServletUtility.sendAuthorizationException(response, e);
                return;
            } catch (BadRequestException e) {
                ServletUtility.sendBadRequest(response, e);
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
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /transactions/{id}
        if (pathInfo.length == 2) {
            try {
                Long transactionId = Long.valueOf(pathInfo[1]);
                Transaction transaction = ServletUtility.gson.fromJson(request.getReader(), Transaction.class);
                transaction = this.transactionService.update(currentUserId, transactionId, transaction);
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, transaction);
                return;
            } catch (IOException e) {
                ServletUtility.sendInternalError(response, e);
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

        ServletUtility.sendBadRequest(response);
        return;
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String[] pathInfo = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
        Long currentUserId = Long.parseLong(String.valueOf(request.getAttribute("currentUserId")));
        String currentUsername = String.valueOf(request.getAttribute("currentUsername"));

        // Handle request: /transactions/{id}
        if (pathInfo.length == 2) {
            try {
                Long transactionId = Long.valueOf(pathInfo[1]);
                this.transactionService.delete(currentUserId, transactionId);
                ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "Transaction deleted.");
                return;
            } catch (AuthorizationException e) {
                ServletUtility.sendAuthorizationException(response, e);
                return;
            } catch (BadRequestException e) {
                ServletUtility.sendBadRequest(response, e);
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
