package com.wawa87.moneystack.service.system.user.dao;

import com.wawa87.moneystack.service.system.exceptions.DatabaseException;
import com.wawa87.moneystack.service.system.user.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    // Create
    Optional<User> save(User user) throws Exception;

    // Read
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll() throws DatabaseException;

    // Update
    int update(User user);

    // Delete
    int deleteById(Long id);
    int delete(User user);
}
