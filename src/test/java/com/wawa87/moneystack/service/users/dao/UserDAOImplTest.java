package com.wawa87.moneystack.service.users.dao;

import com.wawa87.moneystack.service.users.db.PGUtil;
import com.wawa87.moneystack.service.users.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class UserDAOImplTest {
    @Test
    public void testFindById() {
        UserDAOImpl userDAO = new UserDAOImpl(PGUtil.getDataSource());

        Optional<User> user = userDAO.findById(Long.valueOf(1));
        Assertions.assertTrue(user.isPresent());
    }
}
