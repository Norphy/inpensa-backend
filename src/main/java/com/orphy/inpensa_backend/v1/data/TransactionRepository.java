package com.orphy.inpensa_backend.v1.data;

import com.orphy.inpensa_backend.v1.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository {


    public List<Transaction> getAllTransactions();

    public List<Transaction> getAllTransactionsByUser(String userId);

    public Transaction getTransactionById(UUID id);

    public List<Transaction> getTransactionByCatIdAndSubCatId(String userId, UUID catId, UUID subCatId);

//    public Transaction getTransactionByIdAndUser(UUID id, String userId);

    public Transaction saveTransaction(Transaction transaction);

    public void updateTransaction(Transaction transaction);

    public void deleteTransaction(UUID id);
}
