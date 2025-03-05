package com.orphy.inpensa_backend.v1.web.controller;

import com.orphy.inpensa_backend.v1.model.Transaction;
import com.orphy.inpensa_backend.v1.model.dto.TransactionDto;
import com.orphy.inpensa_backend.v1.service.TransactionService;
import com.orphy.inpensa_backend.v1.util.Util;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

//    //TODO authorize for admins only
//    @GetMapping("/users/all/transactions")
//    public List<Transaction> getAllTransactions() {
//        return transactionService.getAllTransactions();
//    }
//
//    @GetMapping("/users/{userId}/transactions")
//    public List<Transaction> getAllTransactionsByUser(@PathVariable String userId) {
//        return transactionService.getAllTransactionsByUser(userId);
//    }
//
//    @GetMapping("/users/{userId}/transactions/{transactionId}")
//    public Transaction getTransactionById(@PathVariable String userId, @PathVariable String transactionId) {
//        UUID transactionUUID =
//                Util.tryOrElseWithoutOpt(() -> UUID.fromString(transactionId),
//                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
//        return transactionService.getTransactionById(userId, transactionUUID);
//    }

    //TODO authorize for admins only
    @GetMapping("/admin/all")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
//
//    //TODO authorize for admins only
    @GetMapping(value = "/admin/user/{userId}")
    public List<Transaction> getAllTransactionsByUser(@PathVariable String userId) {
        return transactionService.getAllTransactionsByUser(userId);
    }

//    @PostMapping("/users/{userId}/transactions")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<Void> saveTransaction(@PathVariable String userId, @RequestBody TransactionDto transaction) {
//        UUID id = transactionService.saveTransaction(transaction);
//        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
//                .buildAndExpand(id.toString()).toUri();
//        return ResponseEntity.created(uri).build();
//    }
//
//    @PutMapping("/users/{userId}/transactions/{transactionId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void> saveTransaction(@PathVariable String userId, @PathVariable String transactionId, @RequestBody Transaction transaction) {
//        UUID transactionUUID = Util.tryOrElseWithoutOpt(() -> UUID.fromString(transactionId),
//                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
//        transactionService.updateTransaction(transactionUUID, transaction);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/users/{userId}/transactions/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void>  deleteTransaction(@PathVariable String userId, @PathVariable String id) {
//        UUID uuid = Util.tryOrElseWithoutOpt(() -> UUID.fromString(id),
//                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
//        transactionService.deleteTransaction(uuid);
//        return ResponseEntity.noContent().build();
//    }

//    //TODO would be obsolete after new pathing
    @GetMapping
    public List<Transaction> getAllTransactionsCurrentUser(@Parameter(hidden = true) SecurityContext context) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) context.getAuthentication();
        return transactionService.getAllTransactionsByUser(token.getName());
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable String id) {
        //TODO Test this applier
        UUID transactionId = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return transactionService.getTransactionById(transactionId);
    }

    @GetMapping(params = {"categoryId", "subCategoryId"})
    public List<Transaction> getTransactionsCurrentUserByCatAndSubCat(@RequestParam String categoryId,
                                                                      @RequestParam String subCategoryId,
                                                                      @Parameter(hidden = true) SecurityContext context) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) context.getAuthentication();
        UUID catUUID = Util.tryOrElse(() -> UUID.fromString(categoryId),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        UUID subCatUUID = Util.tryOrElse(() -> UUID.fromString(subCategoryId),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return transactionService.getTransactionsByCatIdAndSubCatId(token.getName(), catUUID, subCatUUID);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> saveTransaction(@RequestBody TransactionDto transaction) {
        UUID id = transactionService.saveTransaction(transaction);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id.toString()).toUri();
        return ResponseEntity.created(uri).build();
    }

    //TODO authorize for admins only
    @PostMapping("/admin/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void>  saveTransactionWithUser(@RequestBody TransactionDto transaction, @PathVariable String userId) {
        UUID id = transactionService.saveTransaction(transaction, userId);
        URI uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/transactions/{id}")
                .buildAndExpand(id.toString()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> saveTransaction(@RequestBody Transaction transaction) {
        transactionService.updateTransaction(transaction);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void>  deleteTransaction(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        transactionService.deleteTransaction(uuid);
        return ResponseEntity.noContent().build();
    }
}
