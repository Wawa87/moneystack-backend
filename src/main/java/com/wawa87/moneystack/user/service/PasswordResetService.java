package com.wawa87.moneystack.user.service;

import com.wawa87.moneystack.user.dao.PasswordResetDAO;
import com.wawa87.moneystack.user.model.PasswordResetToken;
import com.wawa87.moneystack.user.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class PasswordResetService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();
    private PasswordResetDAO passwordResetDAO;

    public PasswordResetService(PasswordResetDAO passwordResetDAO) {
        this.passwordResetDAO = passwordResetDAO;
    }

    public String generatePlainToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return urlEncoder.encodeToString(randomBytes);
    }

    public String hashToken(String plainToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainToken.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String savePWToken(User user) {
        // Invalidate existing tokens for User by setting usedAt date.
        this.passwordResetDAO.invalidateTokensByUserId(user.getId());

        String plainToken = generatePlainToken();
        String hashToken = hashToken(plainToken);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setTokenHash(hashToken);
        passwordResetToken.setExpiresAt(LocalDateTime.now().plusHours(1L));

        this.passwordResetDAO.save(passwordResetToken);
        return hashToken;
    }

    public Optional<PasswordResetToken> findByHashToken(String hashToken) {
        return this.passwordResetDAO.findByHashToken(hashToken);
    }

    public Long validateToken(String hashString) {
        Optional<PasswordResetToken> prtTokenOpt = this.passwordResetDAO.findByHashToken(hashString);

        if (prtTokenOpt.isEmpty()) return 0L;

        PasswordResetToken prt = prtTokenOpt.get();

        // Check if token is already used or expired.
        if (prt.getUsedAt() != null || prt.getExpiresAt().isBefore(LocalDateTime.now())) return 0L;

        // Invalidate token by setting usedAt.
        prt.setUsedAt(LocalDateTime.now());
        this.passwordResetDAO.update(prt);
        return prt.getUserId();
    }
}
