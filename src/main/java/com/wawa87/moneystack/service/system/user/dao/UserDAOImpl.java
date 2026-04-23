package com.wawa87.moneystack.service.system.user.dao;

import com.wawa87.moneystack.service.system.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

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

    private final Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<User> save(User user) {
        String sql = "INSERT INTO ms_users " +
                "(username, emails, first_name, last_name, phone_number, password_hash)" +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());

            String json = mapper.writeValueAsString(user.getEmails());
            stmt.setString(2, json);

            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getPasswordHash());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    user.setId(resultSet.getLong(1));
                    LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("created_at"), formatter);
                    user.setCreatedAt(createdAt);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT " +
                "id, username, emails, first_name, last_name, phone_number, created_at, updated_at, password_hash " +
                "FROM ms_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT " +
                "id, username, emails, first_name, last_name, phone_number, created_at, updated_at, password_hash " +
                "FROM ms_users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM ms_users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE ms_users SET username=?, emails=?, first_name=?, last_name=?, phone_number=?, updated_at=?, password_hash=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

            stmt.setString(7, user.getPasswordHash());

            stmt.setLong(8, user.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM ms_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int delete(User user) {
        String sql = "DELETE FROM ms_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

        user.setPasswordHash(rs.getString("password_hash"));

        return user;
    }
}
