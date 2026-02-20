package com.wawa87.moneystack.service.users.dao;

import com.wawa87.moneystack.service.users.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    // Create
    Optional<User> save(User user);

    // Read
    Optional<User> findById(Long id);
    List<User> findAll();

    // Update
    User update(User user);
    int updateById(Long id);

    // Delete
    int deleteById(Long id);
    void delete(User user);
}
