package com.orphy.inpensa_backend.service;

import com.orphy.inpensa_backend.model.Transaction;
import com.orphy.inpensa_backend.model.dto.TransactionDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TransactionService {

    public List<Transaction> getAllTransactions();

    public List<Transaction> getAllTransactionsByUser(String userId);

    public Transaction getTransactionByIdAndUser(UUID transactionId);

    public Transaction getTransactionByIdAndUser(UUID transactionId, String userId);

    public UUID saveTransaction(TransactionDto transactionDto);

    public void updateTransaction(UUID transactionId, Transaction transaction);

    public String deleteTransaction(UUID transactionId);
}
