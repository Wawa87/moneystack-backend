package com.wawa87.moneystack.user.dao;

import com.wawa87.moneystack.common.db.PGUtil;
import com.wawa87.moneystack.user.model.PasswordResetToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class PasswordResetDAOImplTest {
    private DataSource dataSource;
    private PasswordResetDAOImpl passwordResetDAO;

    @BeforeEach
    public void prepareConnection() {
        this.dataSource = PGUtil.getDataSource();
        this.passwordResetDAO = new PasswordResetDAOImpl(this.dataSource);
    }

    @AfterEach
    public void endConnection() {
    }

    @Test
    public void testSave() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(Long.valueOf(347));
        passwordResetToken.setTokenHash(token);
        passwordResetToken.setExpiresAt(LocalDateTime.now());

        Optional<PasswordResetToken> passwordResetOpt = this.passwordResetDAO.save(passwordResetToken);

        Assertions.assertTrue(passwordResetOpt.isPresent());
        Assertions.assertNotNull(passwordResetOpt.get().getId());
        Assertions.assertNotNull(passwordResetOpt.get().getCreatedAt());
    }

    @Test
    public void testFindById() {
        Optional<PasswordResetToken> passwordResetOpt = this.passwordResetDAO.findById(Long.valueOf(3));

        Assertions.assertTrue(passwordResetOpt.isPresent());
        Assertions.assertNotNull(passwordResetOpt.get().getId());
        Assertions.assertEquals(Long.valueOf(347), passwordResetOpt.get().getUserId());
        Assertions.assertNotNull(passwordResetOpt.get().getTokenHash());
        Assertions.assertNotNull(passwordResetOpt.get().getExpiresAt());
        Assertions.assertNull(passwordResetOpt.get().getUsedAt());
        Assertions.assertNotNull(passwordResetOpt.get().getCreatedAt());
    }

    @Test
    public void testFindByUserId() {
        List<PasswordResetToken> passwordResetTokenOpt = this.passwordResetDAO.findByUserId(Long.valueOf(347));

        Assertions.assertEquals(2, passwordResetTokenOpt.size());
    }

    @Test
    public void testUpdate() {
        Optional<PasswordResetToken> passwordResetOpt = this.passwordResetDAO.findById(Long.valueOf(3));
        PasswordResetToken passwordResetToken = passwordResetOpt.get();
        passwordResetToken.setTokenHash("NotARealToken");
        passwordResetToken.setUsedAt(LocalDateTime.now());

        this.passwordResetDAO.update(passwordResetToken);

        passwordResetToken = this.passwordResetDAO.findById(Long.valueOf(3)).get();

        Assertions.assertEquals("NotARealToken", passwordResetToken.getTokenHash());
        Assertions.assertNotNull(passwordResetToken.getUsedAt());
    }
}
