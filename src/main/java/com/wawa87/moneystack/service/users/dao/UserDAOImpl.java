package com.wawa87.moneystack.service.users.dao;

import com.wawa87.moneystack.service.users.models.User;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private final Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<User> save(User user) {
        String sql = "INSERT INTO ms_users " +
                "(user_id, emails, first_name, last_name, phone_number)" +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUserId());

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(user.getEmails());
            stmt.setString(2, json);

            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhoneNumber());
            stmt.execute();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                    LocalDateTime createdAt = LocalDateTime.parse(generatedKeys.getString("created_at"), formatter);
                    user.setCreatedAt(createdAt);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT " +
                "id, user_id, emails, first_name, last_name, phone_number, created_at " +
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
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public int updateById(Long id) {
        return 0;
    }

    @Override
    public int deleteById(Long id) {
        return 0;
    }

    @Override
    public void delete(User user) {

    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUserId(rs.getString("user_id"));

        String json = rs.getString("emails");
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> emails = objectMapper.readValue(json, new TypeReference<ArrayList<String>>() {
        });
        user.setEmails(emails);

        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhoneNumber(rs.getString("phone_number"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        user.setCreatedAt(LocalDateTime.parse(rs.getString("created_at"), formatter));

        return user;
    }
}
