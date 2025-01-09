package com.orphy.inpensa_backend.data.impl;

import com.orphy.inpensa_backend.exceptions.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.exceptions.data.UnExpectedException;
import com.orphy.inpensa_backend.model.Transaction;
import com.orphy.inpensa_backend.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql({"/data/schema.sql", "/data/transaction/test-data.sql"})
class TransactionRepositoryJdbcImplTest {
    private final TransactionRepositoryJdbcImpl transactionRepositoryJdbc;

    private static final String USER_ID = "6b246024-59b1-4716-b583-9a0c4d0e5191";
    private static final String USER_ID_TWO = "6b246024-59b1-4716-b583-9a0c4d0e5111";
    private static final UUID CATEGORY_ID = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192");
    private static final UUID SUB_CATEGORY_ID = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193");
    private static final UUID WALLET_ID = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5194");
    private static final UUID TRANSACTION_ID = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5195");
    private static final UUID TRANSACTION_ID_TWO = UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5196");


    @Autowired
    public TransactionRepositoryJdbcImplTest(JdbcTemplate jdbcTemplate) {
        transactionRepositoryJdbc = new TransactionRepositoryJdbcImpl(jdbcTemplate);
    }

    @Test
    public void getTransactionsByUser_HappyPath() {
        //Arrange
        final Transaction expectedTransaction = getExpectedTransactionWithId(TRANSACTION_ID);

        //Act
        List<Transaction> transactionList = transactionRepositoryJdbc.getAllTransactionsByUser(USER_ID);

        //Assert
        assertEquals(1, transactionList.size());
        Transaction actualTransaction = transactionList.getFirst();
        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void getTransactionsByUser_Two_HappyPath() {
        //Arrange
        final Transaction expectedTransaction = getExpectedTransactionWithIdAndUserId(TRANSACTION_ID_TWO, USER_ID_TWO);

        //Act
        List<Transaction> transactionList = transactionRepositoryJdbc.getAllTransactionsByUser(USER_ID_TWO);

        //Assert
        assertEquals(1, transactionList.size());
        Transaction actualTransaction = transactionList.getFirst();
        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void getTransactions_Admin_HappyPath() {
        //Arrange
        final Transaction expectedTransaction = getExpectedTransactionWithIdAndUserId(TRANSACTION_ID, USER_ID);
        final Transaction expectedTransactionTwo = getExpectedTransactionWithIdAndUserId(TRANSACTION_ID_TWO, USER_ID_TWO);

        //Act
        final List<Transaction> transactionList = transactionRepositoryJdbc.getAllTransactions();

        //Assert
        assertEquals(2, transactionList.size());
        assertTrue(transactionList.contains(expectedTransaction));
        assertTrue(transactionList.contains(expectedTransactionTwo));
    }

    @Test
    public void getTransactionsByNonExistingUser_UnHappyPath() {
        //Arrange
        final String userId = "Non Existant";

        //Act
        List<Transaction> transactionList = transactionRepositoryJdbc.getAllTransactionsByUser(userId);

        //Assert
        assertEquals(0, transactionList.size());
    }

    @Test
    public void getTransactionByIdAndByUser_HappyPath() {
        //Arrange
        final Transaction expectedTransaction = getExpectedTransactionWithId(TRANSACTION_ID);

        //Act
        Transaction actualTransaction = transactionRepositoryJdbc.getTransactionByIdAndUser(TRANSACTION_ID, USER_ID);

        //Assert
        assertNotNull(actualTransaction);
        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void getTransactionById_Admin_HappyPath() {
        //Arrange
        final Transaction expectedTransaction = getExpectedTransactionWithId(TRANSACTION_ID);

        //Act
        Transaction actualTransaction = transactionRepositoryJdbc.getTransactionById(TRANSACTION_ID);

        //Assert
        assertNotNull(actualTransaction);
        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void getTransactionByNonExistingId_UnHappyPath() {
        //Arrange
        final UUID nonExisting = UUID.fromString("6b246024-50b1-4706-b503-9a0c4d0e5191");

        //Act && Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionRepositoryJdbc.getTransactionByIdAndUser(nonExisting, USER_ID), "Transaction not found");
    }

    @Test
    public void saveTransaction_HappyPath() {
        //Arrange
        final Transaction saveTransaction = getTransactionWithoutId();

        //Act
        Transaction savedTransaction = transactionRepositoryJdbc.saveTransaction(saveTransaction);
        Transaction expectedTransaction = getExpectedTransactionWithId(savedTransaction.id());

        List<Transaction> transactionList = transactionRepositoryJdbc.getAllTransactionsByUser(USER_ID);
        assertEquals(2, transactionList.size());
        assertEquals(expectedTransaction, savedTransaction);
    }

    @Test
    public void saveInvalidTransaction_UnHappyPath() {
        //Arrange
        final Transaction saveTransaction = getInvalidTransaction();

        //Act && Assert
        assertThrows(DataIntegrityViolationException.class, () -> transactionRepositoryJdbc.saveTransaction(saveTransaction), "NULL not allowed for column \"TAG\"");
    }

    @Test
    public void updateTransaction_HappyPath() {
        //Arrange
        final Transaction oriTransaction = getExpectedTransactionWithId(TRANSACTION_ID);
        final Transaction updateTransaction = updateAmountTransaction(oriTransaction, 60);

        //Act
        transactionRepositoryJdbc.updateTransaction(updateTransaction);

        //Assert
        Transaction actualTransaction = transactionRepositoryJdbc.getTransactionByIdAndUser(TRANSACTION_ID, USER_ID);
        List<Transaction> transactions = transactionRepositoryJdbc.getAllTransactionsByUser(USER_ID);

        assertEquals(60, actualTransaction.amount());
        assertEquals(1, transactions.size());
    }

    @Test
    public void updateNonExistingTransaction_UnHappyPath() {
        //Arrange
        final UUID nonExistentId = UUID.fromString("5b987531-59b1-4716-b583-9a0c4d0e5191");
        final Transaction oriTransaction = getExpectedTransactionWithId(nonExistentId);
        final Transaction updateTransaction = updateAmountTransaction(oriTransaction, 60);

        //Act && Assert
        assertThrows(UnExpectedException.class, () -> transactionRepositoryJdbc.updateTransaction(updateTransaction), "Error updating transaction with id: " + nonExistentId);
    }



    @Test
    public void deleteTransaction_HappyPath() {
        //Arrange

        //Act
        transactionRepositoryJdbc.deleteTransaction(TRANSACTION_ID);

        //Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionRepositoryJdbc.getTransactionByIdAndUser(TRANSACTION_ID, USER_ID), "Transaction not found");
        List<Transaction> transactions = transactionRepositoryJdbc.getAllTransactionsByUser(USER_ID);

        assertEquals(0, transactions.size());
    }


    @Test
    public void deleteNonExistingTransaction_UnHappyPath() {
        //Arrange
        final UUID nonExistentId = UUID.fromString("5b987531-59b1-4716-b583-9a0c4d0e5191");

        //Act
        assertThrows(UnExpectedException.class, () -> transactionRepositoryJdbc.deleteTransaction(nonExistentId), "hi");

        //Assert
        Transaction transaction =  transactionRepositoryJdbc.getTransactionByIdAndUser(TRANSACTION_ID, USER_ID);
        List<Transaction> transactions = transactionRepositoryJdbc.getAllTransactionsByUser(USER_ID);

        assertEquals(56, transaction.amount());
        assertEquals(1, transactions.size());
    }

    private Transaction updateAmountTransaction(Transaction transaction, int newAmount) {
        return new Transaction(transaction.id(), transaction.dateCreated(), transaction.occurrenceDate(), transaction.description(), newAmount, transaction.type(), transaction.tag(),
                transaction.categoryId(), transaction.subCategoryId(), transaction.wallet(), transaction.ownerId());
    }

    private Transaction getInvalidTransaction() {
        return new Transaction(TRANSACTION_ID, 1736154492435L, 1736154492435L, "Description", 56, TransactionType.EXPENSE, null,
                UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5194"), "6b246024-59b1-4716-b583-9a0c4d0e5191");
    }

    private Transaction getExpectedTransactionWithId(UUID id) {
        return new Transaction(id, 1736154492435L, 1736154492435L, "Description", 56, TransactionType.EXPENSE, "TAG",
                UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5194"), "6b246024-59b1-4716-b583-9a0c4d0e5191");
    }

    private Transaction getExpectedTransactionWithIdAndUserId(UUID id, String userId) {
        return new Transaction(id, 1736154492435L, 1736154492435L, "Description", 56, TransactionType.EXPENSE, "TAG",
                UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5194"), userId);
    }

    private Transaction getTransactionWithoutId() {
        return new Transaction(null, 1736154492435L, 1736154492435L, "Description", 56, TransactionType.EXPENSE, "TAG",
                UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5194"), "6b246024-59b1-4716-b583-9a0c4d0e5191");
    }
}