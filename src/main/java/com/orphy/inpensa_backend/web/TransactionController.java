package com.orphy.inpensa_backend.web;

import com.orphy.inpensa_backend.model.Transaction;
import com.orphy.inpensa_backend.model.dto.TransactionDto;
import com.orphy.inpensa_backend.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    //TODO authorize for admins only
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/me")
    public List<Transaction> getAllTransactionsCurrentUser(SecurityContext context) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) context.getAuthentication();
        Jwt principal = (Jwt) token.getPrincipal();
        logger.info("Security Context: {}", context); //TODO remove after testing
        logger.info("Auth: {} Auth Name: {} Token Attributes: {}", token, token.getName(), token.getTokenAttributes()); //TODO remove after testing
        logger.info("Principal: {}", principal); //TODO remove after testing

        String userId = "6b246024-59b1-4716-b583-9a0c4d0e519b"; //TODO get user id
        return transactionService.getAllTransactionsByUser(userId);
    }

    @GetMapping("/me/{id}")
    public Transaction getTransaction(@PathVariable String id) {
        UUID transactionId = UUID.fromString(id);
        String userId = "6b246024-59b1-4716-b583-9a0c4d0e519b"; //TODO get user id
        return transactionService.getTransactionByIdAndUser(transactionId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> saveTransaction(@RequestBody TransactionDto transaction) {
        UUID id = transactionService.saveTransaction(transaction);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id.toString()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> saveTransaction(@PathVariable UUID transactionId, @RequestBody Transaction transaction) {
        transactionService.updateTransaction(transactionId, transaction);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable String id) {
        final UUID uuid = UUID.fromString(id);
        transactionService.deleteTransaction(uuid);
    }


}
