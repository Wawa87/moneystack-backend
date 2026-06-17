package com.wawa87.moneystack.service.system.user;

import com.wawa87.moneystack.service.auth.Argon2Util;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.user.model.User;
import de.mkammerer.argon2.Argon2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class UserServiceTest {
    @Test
    public void testRegisterSuccess() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            Argon2 argon2 = Argon2Util.getArgon2();
            UserService userService = new UserService(userDAO, argon2);

            User user = userService.register(
                    "testUser",
                            "testUser@email.com",
                    "Test",
                    "User",
                    "testpass",
                    "17602221111"
            );

            Assertions.assertNotNull(user);
            Assertions.assertNotNull(user.getId());
            Assertions.assertEquals(user.getUsername(), "testUser");
            Assertions.assertEquals(user.getEmails().get(0), "testUser@email.com");
            Assertions.assertEquals(user.getFirstName(), "Test");
            Assertions.assertEquals(user.getLastName(), "User");
            Assertions.assertTrue(argon2.verify(user.getPasswordHash(), "testpass"));

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAuthenticationSuccess() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            Argon2 argon2 = Argon2Util.getArgon2();
            UserService userService = new UserService(userDAO, argon2);

            User user = userService.register(
                    "testUser",
                    "testUser@email.com",
                    "Test",
                    "User",
                    "testpass",
                    "17602221111"
            );

            Assertions.assertTrue(userService.authenticate("testUser", "testpass"));

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChangePassword() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            UserDAOImpl userDAO = new UserDAOImpl(connection);
            Argon2 argon2 = Argon2Util.getArgon2();
            UserService userService = new UserService(userDAO, argon2);

            User user = userService.register(
                    "testUser",
                    "testUser@email.com",
                    "Test",
                    "User",
                    "testpass",
                    "17602221111"
            );

            Assertions.assertTrue(userService.authenticate(user.getUsername(), "testpass"));

            Assertions.assertFalse(userService.changePassword(user.getUsername(), "testbadoldpass", "newpass"));
            Assertions.assertTrue(userService.changePassword(user.getUsername(), "testpass", "newpass"));

            Optional<User> res = userService.getUser(user.getUsername());
            Assertions.assertTrue(res.isPresent());

            user = res.get();
            Assertions.assertTrue(userService.authenticate(user.getUsername(), "newpass"));

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
