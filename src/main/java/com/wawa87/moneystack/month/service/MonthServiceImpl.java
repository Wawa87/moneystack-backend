package com.wawa87.moneystack.month.service;

import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.month.dao.MonthDAO;
import com.wawa87.moneystack.month.model.Month;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.AttributeInUseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MonthServiceImpl implements MonthService {
    private static final Logger logger = LoggerFactory.getLogger(MonthServiceImpl.class);

    private MonthDAO monthDAO;
    private AuthorizationService authorizationService;

    public MonthServiceImpl(MonthDAO monthDAO, AuthorizationService authorizationService) {
        this.monthDAO = monthDAO;
        this.authorizationService = authorizationService;
    }

    public List<Month> getMonthsByBudgetId(Long budgetId) {
        List<Month> months = this.monthDAO.findByBudgetId(budgetId);
//        months.sort(sortMonth);
        return months;
    }

    public static List<Month> sortMonthsDesc(List<Month> months) {
        months.sort(createMonthComparator().reversed());
        return months;
    }

    private static Comparator<Month> createMonthComparator() {
        return Comparator.comparing(Month::getYear)
                .thenComparing(Month::getMonth);
    }

    public void saveMonth(Month month) {
        monthDAO.save(month);
    }

    @Override
    public Month save(Long requesterId, Month month) throws ValidationException, AuthorizationException, NotFoundException, BadRequestException {
        // Validate Month values.
        if (month.getBudgetId() == null) throw new ValidationException("Budget Id is invalid.");
        if (month.getMonth() == null) throw new ValidationException("Month value is invalid.");
        if (month.getYear() == null) throw new ValidationException("Year value is invalid.");

        // Authorize User.
        if (!authorizationService.authorizeForBudget(requesterId, month.getBudgetId())) throw new AuthorizationException();

        // Save the new Month.
        Optional<Month> monthOpt = monthDAO.save(month);
        if (monthOpt.isEmpty()) throw new BadRequestException("Month failed to save.");
        else return monthOpt.get();
    }

    @Override
    public Month findById(Long requesterId, Long monthId) throws AuthorizationException, NotFoundException {
        // Authorize.
        if (!authorizationService.authorizeForMonth(requesterId, monthId)) throw new AuthorizationException();

        // Get the Month.
        Optional<Month> monthOpt = this.monthDAO.findById(monthId);

        if (monthOpt.isEmpty()) throw new NotFoundException();
        else return monthOpt.get();
    }

    @Override
    public List<Month> findByBudgetId(Long requesterId, Long budgetId) throws AuthorizationException, NotFoundException {
        // Authorize.
        if (!authorizationService.authorizeForBudget(requesterId, budgetId)) throw new AuthorizationException();

        // Get the Months.
        List<Month> months = this.monthDAO.findByBudgetId(budgetId);
        if (months.isEmpty()) throw new NotFoundException();
        else return months;
    }

    @Override
    public Month update(Long requesterId, Long monthId, Month month) throws AttributeInUseException, NotFoundException, ValidationException, BadRequestException {
        // Validate Month values.
        if (month.getBudgetId() == null) throw new ValidationException("Budget Id is invalid.");
        if (month.getMonth() == null) throw new ValidationException("Month value is invalid.");
        if (month.getYear() == null) throw new ValidationException("Year value is invalid.");

        // Authorize.
        if (!authorizationService.authorizeForBudget(requesterId, month.getBudgetId())) throw new AttributeInUseException();

        // Get the Month to update.
        Optional<Month> monthOpt = this.monthDAO.findById(monthId);
        if (monthOpt.isEmpty()) throw new NotFoundException();

        Month monthToUpdate = monthOpt.get();
        monthToUpdate.setYear(month.getYear());
        monthToUpdate.setMonth(month.getMonth());

        // Return the result.
        int result = this.monthDAO.update(monthToUpdate);
        if (result == 1) return monthToUpdate;
        else throw new BadRequestException("Filed to update the Month.");
    }

    @Override
    public void delete(Long requesterId, Long monthId) throws AuthorizationException, NotFoundException, BadRequestException {
        // Authorize.
        if (!authorizationService.authorizeForMonth(requesterId, monthId)) throw new AuthorizationException();

        // Get the requested Month.
        Optional<Month> monthOpt = this.monthDAO.findById(monthId);
        if (monthOpt.isEmpty()) throw new NotFoundException();

        // Delete the Month.
        int result = this.monthDAO.deleteById(monthId);
        if (result == 0) throw new BadRequestException();
    }
}
