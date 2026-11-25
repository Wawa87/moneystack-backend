package com.wawa87.moneystack.user.dao;

import com.wawa87.moneystack.user.model.PasswordResetToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PasswordResetDAOImpl implements PasswordResetDAO {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetDAOImpl.class);
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)  // 0-9 digits, optional
            .toFormatter();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final DataSource dataSource;

    private static final String TABLE = "ms_pw_reset";
    private static final String F_ID = "id";
    private static final String F_USER_ID = "user_id";
    private static final String F_TOKEN_HASH = "token_hash";
    private static final String F_EXPIRES_AT = "expires_at";
    private static final String F_USED_AT = "used_at";
    private static final String F_CREATED_AT = "created_at";

    public PasswordResetDAOImpl(DataSource datasource) {
        this.dataSource = datasource;
    }

    @Override
    public Optional<PasswordResetToken> save(PasswordResetToken passwordResetToken) {
        String sql = "INSERT INTO " + TABLE + " ("
                + F_USER_ID + ","
                + F_TOKEN_HASH + ","
                + F_EXPIRES_AT
                + ") VALUES (?, ?, ?) RETURNING " + F_ID + ", " + F_CREATED_AT;

        try (Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, passwordResetToken.getUserId());
            stmt.setString(2, passwordResetToken.getTokenHash());

            String expiresAtStr = passwordResetToken.getExpiresAt().format(formatter).toString();
            stmt.setTimestamp(3, Timestamp.valueOf(expiresAtStr));

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                passwordResetToken.setId(resultSet.getLong(F_ID));
                passwordResetToken.setCreatedAt(LocalDateTime.parse(resultSet.getString(F_CREATED_AT), formatter));
                return Optional.of(passwordResetToken);
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<PasswordResetToken> findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs  = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<PasswordResetToken> findByHashToken(String hashToken) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_TOKEN_HASH + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashToken);
            try (ResultSet rs  = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<PasswordResetToken> findByUserId(Long userId) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_USER_ID + "=?";

        List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs  = stmt.executeQuery()) {
                while (rs.next()) {
                    passwordResetTokens.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
        return passwordResetTokens;
    }

    @Override
    public int update(PasswordResetToken passwordResetToken) {
        String sql = "UPDATE " + TABLE + " SET "
                + F_TOKEN_HASH + "=?,"
                + F_USED_AT + "=?" + " WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, passwordResetToken.getTokenHash());

            if (passwordResetToken.getUsedAt() == null) {
                stmt.setNull(2, Types.TIMESTAMP);
            } else {
                String usedAtStr = passwordResetToken.getUsedAt().format(formatter).toString();
                stmt.setTimestamp(2, Timestamp.valueOf(usedAtStr));
            }

            stmt.setLong(3, passwordResetToken.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int invalidateTokensByUserId(Long userId) {
        String sql = "UPDATE " + TABLE + " SET "
                + F_USED_AT + "=?"
                + " WHERE " + F_USER_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().format(formatter)));
            stmt.setLong(2, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private PasswordResetToken mapRow(ResultSet rs) throws SQLException {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setId(rs.getLong(F_ID));
        passwordResetToken.setUserId(rs.getLong(F_USER_ID));
        passwordResetToken.setTokenHash(rs.getString(F_TOKEN_HASH));
        if (rs.getString(F_EXPIRES_AT) != null && rs.getString(F_EXPIRES_AT).length() > 0) {
            passwordResetToken.setExpiresAt(LocalDateTime.parse(rs.getString(F_EXPIRES_AT), formatter));
        }
        if (rs.getString(F_USED_AT) != null && rs.getString(F_USED_AT).length() > 0) {
            passwordResetToken.setUsedAt(LocalDateTime.parse(rs.getString(F_USED_AT), formatter));
        }
        if (rs.getString(F_CREATED_AT) != null && rs.getString(F_CREATED_AT).length() > 0) {
            passwordResetToken.setCreatedAt(LocalDateTime.parse(rs.getString(F_CREATED_AT), formatter));
        }

        return passwordResetToken;
    }
}
