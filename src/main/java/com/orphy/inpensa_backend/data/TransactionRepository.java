package com.orphy.inpensa_backend.data;

import com.orphy.inpensa_backend.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {


    public List<Transaction> getAllTransactions();

    public List<Transaction> getAllTransactionsByUser(String userId);

    public Transaction getTransactionById(UUID id);

    public Transaction getTransactionByIdAndUser(UUID id, String userId);

    public Transaction saveTransaction(Transaction transaction);

    public void updateTransaction(Transaction transaction);

    public void deleteTransaction(UUID id);
}
