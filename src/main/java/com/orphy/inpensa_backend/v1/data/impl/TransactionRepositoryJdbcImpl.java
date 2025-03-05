package com.orphy.inpensa_backend.v1.data.impl;

import com.orphy.inpensa_backend.v1.data.TransactionRepository;
import com.orphy.inpensa_backend.v1.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.v1.exceptions.UnExpectedException;
import com.orphy.inpensa_backend.v1.model.Transaction;
import com.orphy.inpensa_backend.v1.model.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepositoryJdbcImpl implements TransactionRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Transaction> getAllTransactions() {
        logger.debug("Get all transactions.");
        final String SQL_GET_ALL_TRANSACTIONS_BY_USER = "SELECT * FROM T_TRANSACTION;";
        return jdbcTemplate.query(SQL_GET_ALL_TRANSACTIONS_BY_USER, TRANSACTION_ROW_MAPPER);
    }

    @Override
    public List<Transaction> getAllTransactionsByUser(String userId) {
        logger.info("Get all transactions by user. User Id: {}", userId); //TODO change to debug
        final String SQL_GET_ALL_TRANSACTIONS_BY_USER = "SELECT * FROM T_TRANSACTION WHERE OWNER_ID = ?;";
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_ALL_TRANSACTIONS_BY_USER);
            ps.setString(1, userId);
            return ps;
        }, TRANSACTION_ROW_MAPPER);
    }

    @Override
    public Transaction getTransactionById(UUID id) {
        logger.debug("Get transaction by id. Transaction id: {}", id);
        final String SQL_GET_SINGLE_TRANSACTION = "SELECT * FROM T_TRANSACTION WHERE ID = ?;";
        List<Transaction> transactions = jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_SINGLE_TRANSACTION);
            ps.setString(1, id.toString());
            return ps;
        }, TRANSACTION_ROW_MAPPER);
        return transactions.stream().findAny().orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    }

    public List<Transaction> getTransactionByCatIdAndSubCatId(String userId, UUID catId, UUID subCatId) {
        logger.debug("Get all transactions by Cat and SubCat. Cat Id: {} SubCat Id: {}", catId, subCatId); //TODO change to debug, replace with Aspect
        final String SQL_GET_ALL_TRANSACTIONS_BY_USER = "SELECT * FROM T_TRANSACTION WHERE OWNER_ID = ? AND CATEGORY_ID = ? AND SUB_CATEGORY_ID = ?;";
        final String catIdStr = catId.toString();
        final String subCatIdStr = subCatId.toString();
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_ALL_TRANSACTIONS_BY_USER);
            ps.setString(1, userId);
            ps.setString(2, catIdStr);
            ps.setString(3, subCatIdStr);
            return ps;
        }, TRANSACTION_ROW_MAPPER);
    }

    //
//    @Override
//    public Transaction getTransactionByIdAndUser(UUID id, String userId) {
//        logger.debug("Get transaction by id and user. Transaction id: {} and user id: {}", id, userId);
//        final String SQL_GET_SINGLE_TRANSACTION = "SELECT * FROM T_TRANSACTION WHERE ID = ? AND OWNER_ID = ?;";
//        List<Transaction> transactions = jdbcTemplate.query(con -> {
//            PreparedStatement ps = con.prepareStatement(SQL_GET_SINGLE_TRANSACTION);
//            ps.setString(1, id.toString());
//            ps.setString(2, userId);
//            return ps;
//        }, TRANSACTION_ROW_MAPPER);
//        return transactions.stream().findAny().orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
//    }

    @Override
    public Transaction saveTransaction(final Transaction transaction) {
        logger.debug("Save new transaction");
        final String SQL_SAVE_TRANSACTION = "INSERT INTO T_TRANSACTION (DATE_CREATED," +
                " OCCURRENCE_DATE, DESCRIPTION, AMOUNT, TRANSACTION_TYPE, TAG, CATEGORY_ID," +
                " SUB_CATEGORY_ID, WALLET_ID, OWNER_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(SQL_SAVE_TRANSACTION, Statement.RETURN_GENERATED_KEYS);

            String categoryId = transaction.categoryId().toString();
            String subCategoryId = transaction.subCategoryId().toString();
            String walletIdStr = transaction.wallet().toString();

            ps.setLong(1, transaction.dateCreated());
            ps.setLong(2, transaction.occurrenceDate());
            ps.setString(3, transaction.description());
            ps.setInt(4, transaction.amount());
            ps.setString(5, transaction.type().toString());
            ps.setString(6, transaction.tag());
            ps.setString(7, categoryId);
            ps.setString(8, subCategoryId);
            ps.setString(9, walletIdStr);
            ps.setString(10, transaction.ownerId());
            return ps;
        }, keyHolder);

        final String idStr = keyHolder.getKeys().values()
                .stream().findAny()
                .orElseThrow(() -> new UnExpectedException("Error getting generated Id.")).toString();
        final UUID id = UUID.fromString(idStr);
        return transaction.getCopyWithId(id);
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        logger.debug("Update transaction with id: {}", transaction.id());
        LoggerFactory.getLogger(getClass()).debug("Save Transaction");
        final String SQL_SAVE_TRANSACTION = "UPDATE T_TRANSACTION SET DATE_CREATED = ?," +
                " OCCURRENCE_DATE = ?, DESCRIPTION = ?, AMOUNT = ?, TRANSACTION_TYPE = ?, TAG = ?, CATEGORY_ID = ?," +
                " SUB_CATEGORY_ID = ?, WALLET_ID = ?, OWNER_ID = ? WHERE ID = ?;";
        int rowsAffected = jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(SQL_SAVE_TRANSACTION);

            String idStr = transaction.id().toString();
            String categoryIdStr = transaction.categoryId().toString();
            String subCategoryIdStr = transaction.subCategoryId().toString();
            String walletIdStr = transaction.wallet().toString();

            ps.setLong(1, transaction.dateCreated());
            ps.setLong(2, transaction.occurrenceDate());
            ps.setString(3, transaction.description());
            ps.setInt(4, transaction.amount());
            ps.setString(5, transaction.type().toString());
            ps.setString(6, transaction.tag());
            ps.setString(7, categoryIdStr);
            ps.setString(8, subCategoryIdStr);
            ps.setString(9, walletIdStr);
            ps.setString(10, transaction.ownerId());
            ps.setString(11, idStr);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error updating transaction with id: " + transaction.id());
        }
    }


    @Override
    public void deleteTransaction(UUID transactionId) {
        final String SQL_DELETE_TRANSACTION = "DELETE FROM T_TRANSACTION WHERE ID = ?;";
        int rowsAffected = jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_TRANSACTION);
            ps.setString(1, transactionId.toString());
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error deleting transaction with id: " + transactionId.toString());
        }
    }

    private final RowMapper<Transaction> TRANSACTION_ROW_MAPPER =
            (rs, rowNum) -> {
                String idStr = rs.getString("ID");
                long dateCreated = rs.getLong("DATE_CREATED");
                long occurrenceDate = rs.getLong("OCCURRENCE_DATE");
                String description = rs.getString("DESCRIPTION");
                int amount = rs.getInt("AMOUNT");
                String transactionType = rs.getString("TRANSACTION_TYPE");
                String tag = rs.getString("TAG");
                String categoryIdStr = rs.getString("CATEGORY_ID");
                String subCategoryIdStr = rs.getString("SUB_CATEGORY_ID");
                String walletStr = rs.getString("WALLET_ID");
                String ownerIdStr = rs.getString("OWNER_ID");
                UUID id = UUID.fromString(idStr);
                UUID categoryId = UUID.fromString(categoryIdStr);
                UUID subCategoryId = UUID.fromString(subCategoryIdStr);
                UUID wallet = UUID.fromString(walletStr);
                return new Transaction(id, dateCreated, occurrenceDate, description, amount,
                        TransactionType.valueOf(transactionType), tag, categoryId, subCategoryId, wallet, ownerIdStr);
            };
}
