package com.wawa87.moneystack.service.system.category.model.dao;

import com.wawa87.moneystack.service.system.category.model.Category;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAOImpl implements CategoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDAOImpl.class);
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)  // 0-9 digits, optional
            .toFormatter();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Connection connection;

    public CategoryDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Category> save(Category category) {
        String sql = "INSERT INTO ms_categories " +
                "(user_id, category_name)" +
                "VALUES(?, ?) RETURNING id, created_at";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, category.getUserId());
            stmt.setString(2, category.getCategoryName());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    category.setId(resultSet.getLong(1));
                    LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("created_at"), formatter);
                    category.setCreatedAt(createdAt);
                    return Optional.of(category);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * " +
                "FROM ms_categories WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Category> findByName(String name) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM ms_categories WHERE LOWER(category_name) LIKE LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Category category = mapRow(resultSet);
                    categories.add(category);
                }
                return categories;
            }
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Category> findByUserId(Long user_id) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM ms_categories WHERE user_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, user_id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Category category = mapRow(resultSet);
                    categories.add(category);
                }
                return categories;
            }
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Category> findByNameAndUserId(String name, Long user_id) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM ms_categories WHERE user_id=? AND LOWER(category_name) LIKE LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, user_id);
            stmt.setString(2, "%" + name + "%");
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Category category = mapRow(resultSet);
                    categories.add(category);
                }
                return categories;
            }
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Category> findByUsername(String username) {
        return List.of();
    }

    @Override
    public List<Category> findByNameAndUsername(String name, String username) {
        return List.of();
    }

    @Override
    public int update(Category category) {
        String sql = "UPDATE ms_categories SET user_id=?, category_name=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, category.getUserId());
            stmt.setString(2, category.getCategoryName());
            stmt.setLong(3, category.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM ms_categories WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Category mapRow(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getLong("id"));
        category.setUserId(resultSet.getLong("user_id"));
        category.setCategoryName(resultSet.getString("category_name"));
        category.setCreatedAt(LocalDateTime.parse(resultSet.getString("created_at"), formatter));

        return category;
    }
}
