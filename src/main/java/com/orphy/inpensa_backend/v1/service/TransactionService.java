package com.orphy.inpensa_backend.v1.service;

import com.orphy.inpensa_backend.v1.data.TransactionRepository;
import com.orphy.inpensa_backend.v1.model.Transaction;
import com.orphy.inpensa_backend.v1.model.Role;
import com.orphy.inpensa_backend.v1.model.dto.TransactionDto;
import com.orphy.inpensa_backend.v1.util.security.annotations.HasAnyWritePermission;
import com.orphy.inpensa_backend.v1.util.security.annotations.IsAdminRead;
import com.orphy.inpensa_backend.v1.util.security.annotations.IsAdminWrite;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @IsAdminRead
    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    @PreAuthorize("@authz.isAdminReadOrCurrentUser(authentication, #userId)")
    public List<Transaction> getAllTransactionsByUser(String userId) {
        return transactionRepository.getAllTransactionsByUser(userId);
    }

    @PostAuthorize("@authz.isAdminReadOrCurrentUser(authentication, returnObject.ownerId)")
    public Transaction getTransactionById(UUID transactionId) {
        //TODO test resource not found vs unauthorized. Users are able to tell if transactions exist or not even if they don't belong to them?
        return transactionRepository.getTransactionById(transactionId);
    }

    @PostAuthorize("@authz.isAdminReadOrCurrentUser(authentication, #userId)")
    public List<Transaction> getTransactionsByCatIdAndSubCatId(String userId, UUID catId, UUID subCatId) {
        return transactionRepository.getTransactionByCatIdAndSubCatId(userId, catId, subCatId);
    }

//    @PreAuthorize("(#userId == principal.name && hasRole('SCOPE_user:read')) || hasRole('SCOPE_admin:read')")
//    public Transaction getTransactionByIdAndUser(UUID transactionId) {
//        return transactionRepository.getTransactionByIdAndUser(transactionId);
//    }

    @HasAnyWritePermission
    public UUID saveTransaction(TransactionDto transactionDto) {
        LoggerFactory.getLogger(getClass()).debug("Save Transaction"); //TODO do Aspect for logging
        long now = Instant.now().toEpochMilli();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        final String userId = auth.getName();
        //TODO change after testing Uncomment above this line
        final String userId = "6b246024-59b1-4716-b583-9a0c4d0e519b";
        final Transaction transaction = new Transaction(null, now, transactionDto.occurrenceDate(),transactionDto.description(), transactionDto.amount(),
                transactionDto.type(), transactionDto.tag(), transactionDto.categoryId(), transactionDto.subCategoryId(), transactionDto.walletId(), userId);
        final Transaction savedTransaction = saveTransaction(transaction);
        return savedTransaction.id();

    }

    @IsAdminWrite
    public UUID saveTransaction(TransactionDto transactionDto, String userId) {
        LoggerFactory.getLogger(getClass()).debug("Save Transaction with user id.");
        long now = Instant.now().toEpochMilli();
        final Transaction transaction = new Transaction(null, now, transactionDto.occurrenceDate(),transactionDto.description(), transactionDto.amount(),
                transactionDto.type(), transactionDto.tag(), transactionDto.categoryId(), transactionDto.subCategoryId(), transactionDto.walletId(), userId);
        final Transaction savedTransaction = saveTransaction(transaction);
        return savedTransaction.id();

    }

    @Transactional
    @PreAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, #transaction.ownerId)")
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    @SuppressWarnings("UnusedReturnValue")
    public String updateTransaction(Transaction transaction) {
        Transaction toBeUpdated = transactionRepository.getTransactionById(transaction.id());
        transactionRepository.updateTransaction(transaction);
        return toBeUpdated.ownerId();
    }

    /**
     * This method deletes a transaction. {@code @Transaction} is needed in order to roll back deletion if {@link org.springframework.security.access.AccessDeniedException}
     * is thrown by authorization.
     * <P><B>Security:</B> Only users that own the transaction to be deleted and have the {@link Role#USER_WRITE} Role or
     * Admin users that have the {@link Role#ADMIN_WRITE} Role can delete a transaction.<P>
     * @param transactionId is the ID of the transaction to be deleted.
     * @return ID of the user that owns the transaction, used for authorization.
     */
    @Transactional
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    @SuppressWarnings("UnusedReturnValue")
    public String deleteTransaction(UUID transactionId) {
        Transaction toBeDeleted = transactionRepository.getTransactionById(transactionId);
        transactionRepository.deleteTransaction(transactionId);
        return toBeDeleted.ownerId();
    }

    private Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.saveTransaction(transaction);
    }

}
