package com.wawa87.moneystack.subcategory.dao;

import com.wawa87.moneystack.subcategory.model.Subcategory;
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

public class SubcategoryDAOImpl implements SubcategoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(SubcategoryDAOImpl.class);
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)  // 0-9 digits, optional
            .toFormatter();
    private static final ObjectMapper mapper = new ObjectMapper();

    private final DataSource dataSource;

    private static final String TABLE = "ms_subcategories";
    private static final String F_ID = "id";
    private static final String F_CATEGORY_ID = "category_id";
    private static final String F_NAME = "name";
    private static final String F_DESCRIPTION = "description";

    public SubcategoryDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Subcategory> save(Subcategory subcategory) {
        String sql = "INSERT INTO " + TABLE + " ("
            + F_CATEGORY_ID + ","
            + F_NAME + ","
            + F_DESCRIPTION + ") VALUES(?, ?, ?) RETURNING " + F_ID;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, subcategory.getCategoryId());
            stmt.setString(2, subcategory.getName());

            if (subcategory.getDescription() == null) {
                stmt.setNull(3, Types.NULL);
            } else {
                stmt.setString(3, subcategory.getDescription());
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    subcategory.setId(resultSet.getLong(1));
                    return Optional.of(subcategory);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Subcategory> findById(Long id) {
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
    public List<Subcategory> findByCategoryId(Long categoryId) {
        List<Subcategory> subcategories = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_CATEGORY_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Subcategory subcategory = mapRow(resultSet);
                    subcategories.add(subcategory);
                }
                return subcategories;
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int update(Subcategory subcategory) {
        String sql = "UPDATE " + TABLE + " SET " + F_CATEGORY_ID + "=?, " + F_NAME + "=?, " + F_DESCRIPTION + "=? WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, subcategory.getCategoryId());
            stmt.setString(2, subcategory.getName());

            if (subcategory.getDescription() == null) {
                stmt.setNull(3, Types.NULL);
            } else {
                stmt.setString(3, subcategory.getDescription());
            }

            stmt.setLong(4, subcategory.getId());

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

    private Subcategory mapRow(ResultSet resultSet) throws SQLException {
        Subcategory subcategory = new Subcategory();
        subcategory.setId(resultSet.getLong(F_ID));
        subcategory.setCategoryId(resultSet.getLong(F_CATEGORY_ID));
        subcategory.setName(resultSet.getString(F_NAME));
        subcategory.setDescription(resultSet.getString(F_DESCRIPTION));

        return subcategory;
    }
}
