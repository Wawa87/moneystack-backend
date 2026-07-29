package com.wawa87.moneystack.month.service;

import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.month.model.Month;

import javax.naming.directory.AttributeInUseException;
import java.util.List;

public interface MonthService {
    public Month save(Long requesterId, Month month) throws ValidationException, AuthorizationException, NotFoundException, BadRequestException;
    public Month findById(Long requesterId, Long monthId) throws AuthorizationException, NotFoundException;
    public List<Month> findByBudgetId(Long requesterId, Long budgetId) throws AuthorizationException, NotFoundException;
    public Month update(Long requesterId, Long monthId, Month month) throws AttributeInUseException, NotFoundException, ValidationException, BadRequestException;
    public void delete(Long requesterId, Long monthId) throws AuthorizationException, NotFoundException, BadRequestException;
}
