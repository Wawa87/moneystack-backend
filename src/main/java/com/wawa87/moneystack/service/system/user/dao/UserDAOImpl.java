package com.wawa87.moneystack.service.system.user.dao;

import com.wawa87.moneystack.service.system.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
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

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)  // 0-9 digits, optional
            .toFormatter();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final DataSource dataSource;

    private static final String TABLE = "ms_users";
    private static final String F_ID = "id";
    private static final String F_USERNAME = "username";
    private static final String F_EMAILS = "emails";
    private static final String F_FIRST_NAME = "first_name";
    private static final String F_LAST_NAME = "last_name";
    private static final String F_PHONE_NUMBER = "phone_number";
    private static final String F_PASSWORD_HASH = "password_hash";
    private static final String F_CREATED_AT = "created_at";
    private static final String F_UPDATED_AT = "updated_at";

    public UserDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> save(User user) {
        String sql = "INSERT INTO " + TABLE + " ("
                + F_USERNAME + ","
                + F_EMAILS + ","
                + F_FIRST_NAME + ","
                + F_LAST_NAME + ","
                + F_PHONE_NUMBER + ","
                + F_PASSWORD_HASH
                + ") VALUES (?, ?, ?, ?, ?, ?) RETURNING " + F_ID + ", " + F_CREATED_AT;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());

            String json = mapper.writeValueAsString(user.getEmails());
            stmt.setString(2, json);

            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getPassword());

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
                LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("created_at"), formatter);
                user.setCreatedAt(createdAt);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
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
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_USERNAME + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
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
    public List<User> findAll() {
        String sql = "SELECT * FROM " + TABLE;

        List<User> users = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return users;
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE " + TABLE + " SET "
            + F_USERNAME + "=?,"
            + F_EMAILS + "=?,"
            + F_FIRST_NAME + "=?,"
            + F_LAST_NAME + "=?,"
            + F_PHONE_NUMBER + "=?,"
            + F_UPDATED_AT + "=?,"
            + F_PASSWORD_HASH + "=? WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());

            String json = mapper.writeValueAsString(user.getEmails());
            stmt.setString(2, json);

            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhoneNumber());

            LocalDateTime updatedAt = LocalDateTime.now();
            user.setUpdatedAt(updatedAt);
            String updatedAtStr = updatedAt.format(formatter).toString();
            stmt.setTimestamp(6, Timestamp.valueOf(updatedAt));

            stmt.setString(7, user.getPassword());

            stmt.setLong(8, user.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE " + F_ID + "= ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int delete(User user) {
        String sql = "DELETE FROM " + TABLE + " WHERE " + F_ID + "= ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, user.getId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));

        String json = rs.getString("emails");
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> emails = objectMapper.readValue(json, new TypeReference<ArrayList<String>>() {
        });
        user.setEmails(emails);

        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhoneNumber(rs.getString("phone_number"));

        user.setCreatedAt(LocalDateTime.parse(rs.getString("created_at"), formatter));

        if (rs.getString("updated_at") != null && rs.getString("updated_at").length() > 0) {
            user.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at"), formatter));
        }

        user.setPassword(rs.getString("password_hash"));

        return user;
    }
}
