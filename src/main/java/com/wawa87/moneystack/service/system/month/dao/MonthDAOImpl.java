package com.wawa87.moneystack.service.system.month.dao;

import com.wawa87.moneystack.service.system.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.month.model.Month;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MonthDAOImpl implements MonthDAO {
    private static final Logger logger = LoggerFactory.getLogger(MonthDAOImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final DataSource dataSource;

    private static final String TABLE = "ms_months";
    private static final String F_ID = "id";
    private static final String F_BUDGET_ID = "budget_id";
    private static final String F_YEAR = "year";
    private static final String F_MONTH = "month";

    public MonthDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Month> save(Month month) {
        String sql = "INSERT INTO " + TABLE + " ("
            + F_BUDGET_ID + ","
            + F_YEAR + ","
            + F_MONTH
            + ") VALUES(?, ?, ?) RETURNING id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, month.getBudgetId());
            stmt.setInt(2, month.getYear().getValue());
            stmt.setInt(3, month.getMonth().getValue());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    month.setId(resultSet.getLong(1));
                    return Optional.of(month);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Month> findById(Long id) {
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
    public List<Month> findByBudgetId(Long budgetId) {
        List<Month> months = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_BUDGET_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, budgetId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Month month = mapRow(resultSet);
                    months.add(month);
                }
            }
            return months;
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int update(Month month) {
        String sql = "UPDATE " + TABLE + " SET "
                + F_BUDGET_ID + "=?,"
                + F_YEAR + "=?,"
                + F_MONTH + "=?"
                + " WHERE " + F_ID + "=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, month.getBudgetId());
            stmt.setInt(2, month.getYear().getValue());
            stmt.setInt(3, month.getMonth().getValue());
            stmt.setLong(4, month.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id=?";

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
    public int delete(Month month) {
        String sql = "DELETE FROM " + TABLE + " WHERE id=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, month.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Month mapRow(ResultSet resultSet) throws SQLException {
        Month month = new Month();
        month.setId(resultSet.getLong(F_ID));
        month.setBudgetId(resultSet.getLong(F_BUDGET_ID));
        month.setYear(Year.of(resultSet.getInt(F_YEAR)));
        month.setMonth(java.time.Month.of(resultSet.getInt(F_MONTH)));

        return month;
    }
}
