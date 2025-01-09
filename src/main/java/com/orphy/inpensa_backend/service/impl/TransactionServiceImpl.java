package com.orphy.inpensa_backend.service.impl;

import com.orphy.inpensa_backend.data.TransactionRepository;
import com.orphy.inpensa_backend.model.Transaction;
import com.orphy.inpensa_backend.model.dto.TransactionDto;
import com.orphy.inpensa_backend.service.TransactionService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    //TODO authorize only admin
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    @Override
    public List<Transaction> getAllTransactionsByUser(String userId) {
        return transactionRepository.getAllTransactionsByUser(userId);
    }

    //TODO authorize only admin
    @Override
    public Transaction getTransactionByIdAndUser(UUID transactionId) {
        return transactionRepository.getTransactionById(transactionId);
    }

    @PreAuthorize("#userId == principal.name")
    @Override
    public Transaction getTransactionByIdAndUser(UUID transactionId, String userId) {

        return transactionRepository.getTransactionByIdAndUser(transactionId, userId);
    }

    @Override
    public UUID saveTransaction(TransactionDto transactionDto) {
        LoggerFactory.getLogger(getClass()).debug("Save Transaction");
        long now = Instant.now().toEpochMilli();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String userId = auth.getName();
        final Transaction transaction = new Transaction(null, now, transactionDto.occurrenceDate(),transactionDto.description(), transactionDto.amount(),
                transactionDto.type(), transactionDto.tag(), transactionDto.categoryId(), transactionDto.subCategoryId(), transactionDto.wallet(), userId);
        final Transaction savedTransaction = transactionRepository.saveTransaction(transaction);
        return savedTransaction.id();

    }

    @PreAuthorize("#transaction.ownerId == principal.name")
    @Override
    public void updateTransaction(UUID transactionId, Transaction transaction) {
        if(transactionId != transaction.id()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        transactionRepository.updateTransaction(transaction);
    }

    //TODO post authorize and check if transactional is needed
    @Override
    @Transactional
    @PostAuthorize("returnObject == principal.name")
    public String deleteTransaction(UUID transactionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String userId = auth.getName();
        //TODO test against resource not found exception
        Transaction toBeDeleted = transactionRepository.getTransactionByIdAndUser(transactionId, userId);
        transactionRepository.deleteTransaction(transactionId);
        return toBeDeleted.ownerId();
    }
}
