package com.wawa87.moneystack.budget.dao;

import com.wawa87.moneystack.budget.model.Budget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BudgetDAOImpl implements BudgetDAO {
    private static final Logger logger = LoggerFactory.getLogger(BudgetDAOImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final DataSource dataSource;

    private static final String TABLE = "ms_budgets";
    private static final String F_ID = "id";
    private static final String F_USER_ID = "user_id";
    private static final String F_NAME = "name";
    private static final String F_ISACTIVE = "is_active";

    public BudgetDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Budget> save(Budget budget) {
        String sql = "INSERT INTO " + TABLE + " ("
            + F_USER_ID + ","
            + F_NAME + ","
            + F_ISACTIVE
            + ") VALUES(?, ?, ?) RETURNING id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, budget.getUserId());
            stmt.setString(2, budget.getName());
            stmt.setBoolean(3, budget.getActive());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    budget.setId(resultSet.getLong(1));
                    return Optional.of(budget);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Budget> findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE id=?";

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
    public List<Budget> findByUsername(String username) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT " + TABLE + ".* FROM " + TABLE + ", ms_users WHERE ms_users.username = ? AND " + TABLE + ".user_id = ms_users.id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Budget budget = mapRow(resultSet);
                    budgets.add(budget);
                }
            }
            return budgets;
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Budget> findByUserId(Long userId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_USER_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Budget budget = mapRow(resultSet);
                    budgets.add(budget);
                }
            }
            return budgets;
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Budget> findActiveByUserId(Long userId) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_USER_ID +"=? AND " + F_ISACTIVE + "=true";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
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
    public int update(Budget budget) {
        String sql = "UPDATE " + TABLE + " SET "
            + F_USER_ID + "=?,"
            + F_NAME + "=?,"
            + F_ISACTIVE + "=? WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, budget.getUserId());
            stmt.setString(2, budget.getName());
            stmt.setBoolean(3, budget.getActive());
            stmt.setLong(4, budget.getId());

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

    @Override
    public int delete(Budget budget) {
        String sql = "DELETE FROM " + TABLE + " WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, budget.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Budget mapRow(ResultSet resultSet) throws SQLException {
        Budget budget = new Budget();
        budget.setId(resultSet.getLong(F_ID));
        budget.setUserId(resultSet.getLong(F_USER_ID));
        budget.setName(resultSet.getString(F_NAME));
        budget.setActive(resultSet.getBoolean(F_ISACTIVE));

        return budget;
    }
}
