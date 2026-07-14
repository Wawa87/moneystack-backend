package com.wawa87.moneystack.service.system.category.dao;

import com.wawa87.moneystack.service.system.category.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.*;
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

    private final DataSource dataSource;

    private static final String TABLE = "ms_categories";
    private static final String F_ID = "id";
    private static final String F_USER_ID = "user_id";
    private static final String F_NAME = "name";
    private static final String F_DESCRIPTION = "description";

    public CategoryDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Category> save(Category category) {
        String sql = "INSERT INTO " + TABLE + " ("
        + F_USER_ID + ", "
        + F_NAME + ", "
        + F_DESCRIPTION + ") VALUES(?, ?, ?) RETURNING " + F_ID;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, category.getUserId());
            stmt.setString(2, category.getName());

            if (category.getDescription() == null) {
                stmt.setNull(3, Types.NULL);
            } else {
                stmt.setString(3, category.getDescription());
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    category.setId(resultSet.getLong(1));
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
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM " + TABLE + " WHERE LOWER(" + F_NAME + ") LIKE LOWER(?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_USER_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public List<Category> findByNameAndUserId(String name, Long userId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_USER_ID + "=? AND LOWER(" + F_NAME + ") LIKE LOWER(?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
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
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT " + TABLE + ".* FROM " + TABLE + ", ms_users WHERE ms_users.username = ? AND " + TABLE + ".user_id = ms_users.id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
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
    public int update(Category category) {
        String sql = "UPDATE " + TABLE + " SET " + F_USER_ID + "=?, " + F_NAME + "=?, " + F_DESCRIPTION + "=? WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, category.getUserId());
            stmt.setString(2, category.getName());

            if (category.getDescription() == null) {
                stmt.setNull(3, Types.NULL);
            } else {
                stmt.setString(3, category.getDescription());
            }

            stmt.setLong(4, category.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Category mapRow(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getLong(F_ID));
        category.setUserId(resultSet.getLong(F_USER_ID));
        category.setName(resultSet.getString(F_NAME));
        category.setDescription(resultSet.getString(F_DESCRIPTION));

        return category;
    }
}
