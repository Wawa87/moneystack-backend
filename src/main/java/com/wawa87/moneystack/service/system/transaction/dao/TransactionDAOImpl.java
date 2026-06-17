package com.wawa87.moneystack.service.system.transaction.dao;

import com.wawa87.moneystack.service.system.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.service.system.month.model.Month;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDAOImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Connection connection;

    private static final String TABLE = "ms_transactions";
    private static final String F_ID = "id";
    private static final String F_MONTH_ID = "month_id";
    private static final String F_CATEGORY_ID = "category_id";
    private static final String F_SUBCATEGORY_ID = "subcategory_id";
    private static final String F_DESCRIPTION = "description";
    private static final String F_TIMESTAMP = "timestamp";
    private static final String F_AMOUNT = "amount";

    public TransactionDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Transaction> save(Transaction transaction) {
        String sql = "INSERT INTO " + TABLE + " ("
                + F_MONTH_ID + ","
                + F_CATEGORY_ID + ","
                + F_SUBCATEGORY_ID + ","
                + F_DESCRIPTION + ","
                + F_TIMESTAMP + ","
                + F_AMOUNT + ") VALUES(?, ?, ?, ?, ?, ?) RETURNING " + F_ID;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, transaction.getMonthId());
            stmt.setLong(2, transaction.getCategoryId());
            stmt.setLong(3, transaction.getSubcategoryId());
            stmt.setString(4, transaction.getDescription());
            stmt.setString(5, transaction.getTimestamp().toString());
            stmt.setBigDecimal(6, transaction.getAmount());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    transaction.setId(resultSet.getLong(1));
                    return Optional.of(transaction);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_ID + "=?";

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
    public List<Transaction> findByMonthId(Long monthId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE " + F_MONTH_ID + "=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, monthId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = mapRow(resultSet);
                    transactions.add(transaction);
                }
            }
            return transactions;
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int update(Transaction transaction) {
        String sql = "UPDATE " + TABLE + " SET "
                + F_MONTH_ID + "=?,"
                + F_CATEGORY_ID + "=?,"
                + F_SUBCATEGORY_ID + "=?,"
                + F_DESCRIPTION + "=?,"
                + F_TIMESTAMP + "=?,"
                + F_AMOUNT + "=?"
                + " WHERE " + F_ID + "=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, transaction.getMonthId());
            stmt.setLong(2, transaction.getCategoryId());
            stmt.setLong(3, transaction.getSubcategoryId());
            stmt.setString(4, transaction.getDescription());
            stmt.setString(5, transaction.getTimestamp().toString());
            stmt.setBigDecimal(6, transaction.getAmount());
            stmt.setLong(7, transaction.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int delete(Transaction transaction) {
        String sql = "DELETE FROM " + TABLE + " WHERE id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, transaction.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Transaction mapRow(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getLong(F_ID));
        transaction.setMonthId(resultSet.getLong(F_MONTH_ID));
        transaction.setCategoryId(resultSet.getLong(F_CATEGORY_ID));
        transaction.setSubcategoryId(resultSet.getLong(F_SUBCATEGORY_ID));
        transaction.setDescription(resultSet.getString(F_DESCRIPTION));
        transaction.setTimestamp(LocalDateTime.parse(resultSet.getString(F_TIMESTAMP)));
        transaction.setAmount(resultSet.getBigDecimal(F_AMOUNT));

        return transaction;
    }
}
