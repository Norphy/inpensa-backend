package com.orphy.inpensa_backend.v1.web.controller;

import com.orphy.inpensa_backend.v1.model.Wallet;
import com.orphy.inpensa_backend.v1.model.dto.WalletDto;
import com.orphy.inpensa_backend.v1.service.WalletService;
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
@RequestMapping("/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/admin/all")
    public List<Wallet> getAllWallets() {
        return walletService.getAllWallets();
    }

    @GetMapping
    public List<Wallet> getAllWalletsCurrentUser(@Parameter(hidden = true) SecurityContext context) {
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) context.getAuthentication();
        String id = jwtAuthToken.getName();
        return walletService.getAllWalletsByUser(id);
    }

    @GetMapping("/admin/user/{userId}")
    public List<Wallet> getAllWalletsCurrentUser(@PathVariable String userId) {
        return walletService.getAllWalletsByUser(userId);
    }

    @GetMapping("/{id}")
    public Wallet getWalletById(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return walletService.getWalletById(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createWallet(@RequestBody WalletDto walletDto) {
        UUID uuid = walletService.saveWallet(walletDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(uuid).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/admin/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createWalletWithUser(@RequestBody WalletDto walletDto,
                                                     @PathVariable String userId) {
        UUID uuid = walletService.saveWallet(walletDto, userId);
        URI uri = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("/wallets/{id}")
                .buildAndExpand(uuid).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateWallet(@RequestBody Wallet wallet) {
        walletService.update(wallet);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void>  deleteWallet(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        walletService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
