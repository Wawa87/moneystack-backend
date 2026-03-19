package com.wawa87.moneystack.service.users;

import com.wawa87.moneystack.service.auth.Argon2Util;
import com.wawa87.moneystack.service.users.dao.UserDAOImpl;
import com.wawa87.moneystack.service.users.db.PGUtil;
import com.wawa87.moneystack.service.users.models.User;
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
            Assertions.assertEquals(user.getUserId(), "testUser");
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

            Assertions.assertTrue(userService.authenticate(user.getUserId(), "testpass"));

            Assertions.assertFalse(userService.changePassword(user.getUserId(), "testbadoldpass", "newpass"));
            Assertions.assertTrue(userService.changePassword(user.getUserId(), "testpass", "newpass"));

            Optional<User> res = userService.getUser(user.getUserId());
            Assertions.assertTrue(res.isPresent());

            user = res.get();
            Assertions.assertTrue(userService.authenticate(user.getUserId(), "newpass"));

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
