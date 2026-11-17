package com.wawa87.moneystack.user.dao;

import com.wawa87.moneystack.user.model.PasswordResetToken;

import java.util.List;
import java.util.Optional;

public interface PasswordResetDAO {
    // Create
    Optional<PasswordResetToken> save(PasswordResetToken passwordResetToken);

    // Read
    Optional<PasswordResetToken> findById(Long id);
    Optional<PasswordResetToken> findByHashToken(String hashToken);
    List<PasswordResetToken> findByUserId(Long userId);

    // Update
    int update(PasswordResetToken passwordResetToken);
}
