package com.wawa87.moneystack;

import com.wawa87.moneystack.auth.util.Argon2Util;
import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.auth.service.AuthorizationServiceImpl;
import com.wawa87.moneystack.auth.util.JwtUtil;
import com.wawa87.moneystack.budget.BudgetService;
import com.wawa87.moneystack.budget.dao.BudgetDAO;
import com.wawa87.moneystack.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.category.CategoryService;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.common.db.PGUtil;
import com.wawa87.moneystack.month.MonthService;
import com.wawa87.moneystack.month.dao.MonthDAO;
import com.wawa87.moneystack.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.subcategory.SubcategoryService;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAOImpl;
import com.wawa87.moneystack.transaction.TransactionService;
import com.wawa87.moneystack.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.transaction.dao.TransactionDAOImpl;
import com.wawa87.moneystack.user.UserService;
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

    AuthorizationService authorizationService;
    UserService userService;
    BudgetService budgetService;
    CategoryService categoryService;
    SubcategoryService subcategoryService;
    MonthService monthService;
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

        this.authorizationService = new AuthorizationServiceImpl(
                this.userDAO, this.categoryDAO, this.subcategoryDAO, this.budgetDAO, this.monthDAO, this.transactionDAO);
        this.userService = new UserService(this.userDAO, this.argon2, this.authorizationService);
        this.budgetService = new BudgetService(this.budgetDAO);
        this.categoryService = new CategoryService(this.categoryDAO);
        this.subcategoryService = new SubcategoryService(this.subcategoryDAO);
        this.monthService = new MonthService(this.monthDAO);
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

    public BudgetService getBudgetService() {
        return budgetService;
    }

    public void setBudgetService(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public SubcategoryService getSubcategoryService() {
        return subcategoryService;
    }

    public void setSubcategoryService(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    public MonthService getMonthService() {
        return monthService;
    }

    public void setMonthService(MonthService monthService) {
        this.monthService = monthService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
