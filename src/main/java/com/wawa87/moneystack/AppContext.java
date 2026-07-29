package com.wawa87.moneystack;

import com.wawa87.moneystack.auth.service.AuthenticationService;
import com.wawa87.moneystack.auth.service.AuthenticationServiceImpl;
import com.wawa87.moneystack.auth.util.Argon2Util;
import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.auth.service.AuthorizationServiceImpl;
import com.wawa87.moneystack.auth.util.JwtUtil;
import com.wawa87.moneystack.budget.service.BudgetServiceImpl;
import com.wawa87.moneystack.budget.dao.BudgetDAO;
import com.wawa87.moneystack.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.category.service.CategoryServiceImpl;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.common.db.PGUtil;
import com.wawa87.moneystack.month.service.MonthServiceImpl;
import com.wawa87.moneystack.month.dao.MonthDAO;
import com.wawa87.moneystack.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.subcategory.service.SubcategoryServiceImpl;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAOImpl;
import com.wawa87.moneystack.transaction.service.TransactionService;
import com.wawa87.moneystack.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.transaction.dao.TransactionDAOImpl;
import com.wawa87.moneystack.user.service.UserService;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.dao.UserDAOImpl;
import de.mkammerer.argon2.Argon2;

import javax.sql.DataSource;
import java.util.Properties;

public class AppContext {
    private DataSource dataSource;
    private Argon2 argon2;
    private Loader loader;
    private Properties properties;
    private JwtUtil jwtUtil;

    UserDAO userDAO;
    BudgetDAO budgetDAO;
    CategoryDAO categoryDAO;
    SubcategoryDAO subcategoryDAO;
    MonthDAO monthDAO;
    TransactionDAO transactionDAO;

    AuthenticationService authenticationService;
    AuthorizationService authorizationService;
    UserService userService;
    BudgetServiceImpl budgetService;
    CategoryServiceImpl categoryService;
    SubcategoryServiceImpl subcategoryService;
    MonthServiceImpl monthService;
    TransactionService transactionService;

    public AppContext() {
        this.dataSource = PGUtil.getDataSource();
        this.argon2 = Argon2Util.getArgon2();
        this.loader = new Loader();
        this.properties = loader.loadPropertiesFile("application.properties");
        this.jwtUtil = new JwtUtil(properties.getProperty("JWT_SECRET"), properties.getProperty("JWT_SECRET"));

        this.userDAO = new UserDAOImpl(this.dataSource);
        this.budgetDAO = new BudgetDAOImpl(this.dataSource);
        this.categoryDAO = new CategoryDAOImpl(this.dataSource);
        this.subcategoryDAO = new SubcategoryDAOImpl(this.dataSource);
        this.monthDAO = new MonthDAOImpl(this.dataSource);
        this.transactionDAO = new TransactionDAOImpl(this.dataSource);

        this.authenticationService = new AuthenticationServiceImpl(this.argon2, this.userDAO);
        this.authorizationService = new AuthorizationServiceImpl(
                this.userDAO, this.categoryDAO, this.subcategoryDAO, this.budgetDAO, this.monthDAO, this.transactionDAO);

        this.userService = new UserService(this.userDAO, this.argon2, this.authenticationService, this.authorizationService);
        this.budgetService = new BudgetServiceImpl(this.budgetDAO, this.authorizationService);
        this.categoryService = new CategoryServiceImpl(this.categoryDAO, this.authorizationService);
        this.subcategoryService = new SubcategoryServiceImpl(this.subcategoryDAO, this.authorizationService);
        this.monthService = new MonthServiceImpl(this.monthDAO, this.authorizationService);
        this.transactionService = new TransactionService(this.transactionDAO, this.categoryService, this.subcategoryService);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Argon2 getArgon2() {
        return argon2;
    }

    public void setArgon2(Argon2 argon2) {
        this.argon2 = argon2;
    }

    public Loader getLoader() {
        return loader;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public JwtUtil getJwtUtil() {
        return jwtUtil;
    }

    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public BudgetDAO getBudgetDAO() {
        return budgetDAO;
    }

    public void setBudgetDAO(BudgetDAO budgetDAO) {
        this.budgetDAO = budgetDAO;
    }

    public CategoryDAO getCategoryDAO() {
        return categoryDAO;
    }

    public void setCategoryDAO(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public SubcategoryDAO getSubcategoryDAO() {
        return subcategoryDAO;
    }

    public void setSubcategoryDAO(SubcategoryDAO subcategoryDAO) {
        this.subcategoryDAO = subcategoryDAO;
    }

    public MonthDAO getMonthDAO() {
        return monthDAO;
    }

    public void setMonthDAO(MonthDAO monthDAO) {
        this.monthDAO = monthDAO;
    }

    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public BudgetServiceImpl getBudgetService() {
        return budgetService;
    }

    public void setBudgetService(BudgetServiceImpl budgetService) {
        this.budgetService = budgetService;
    }

    public CategoryServiceImpl getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    public SubcategoryServiceImpl getSubcategoryService() {
        return subcategoryService;
    }

    public void setSubcategoryService(SubcategoryServiceImpl subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    public MonthServiceImpl getMonthService() {
        return monthService;
    }

    public void setMonthService(MonthServiceImpl monthService) {
        this.monthService = monthService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
