package com.orphy.inpensa_backend.v1.service;

import com.orphy.inpensa_backend.v1.data.WalletRepository;
import com.orphy.inpensa_backend.v1.model.Wallet;
import com.orphy.inpensa_backend.v1.model.dto.WalletDto;
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
public class WalletService {

    private final WalletRepository walletRepository;
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @IsAdminRead
    public List<Wallet> getAllWallets() {
        return walletRepository.getAllWallets();
    }

    @PreAuthorize("@authz.isAdminReadOrCurrentUser(authentication, #userId)")
    public List<Wallet> getAllWalletsByUser(String userId) {
        return walletRepository.getAllWalletsByUser(userId);
    }

    @PreAuthorize("@authz.isAdminReadOrCurrentUser(authentication, returnObject.ownerId)")
    public Wallet getWalletById(UUID id) {
        return walletRepository.getWalletById(id);
    }

    @HasAnyWritePermission
    public UUID saveWallet(WalletDto walletDto) {
        LoggerFactory.getLogger(getClass()).debug("Save Wallet"); //TODO do Aspect for logging
        long now = Instant.now().toEpochMilli();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String userId = auth.getName();
        final Wallet wallet = new Wallet(null, walletDto.name(), now, walletDto.amount(), userId);
        final Wallet savedWallet = saveWallet(wallet);
        return savedWallet.id();
    }

    @IsAdminWrite
    public UUID saveWallet(WalletDto walletDto, String userId) {
        LoggerFactory.getLogger(getClass()).debug("Save Wallet"); //TODO do Aspect for logging
        long now = Instant.now().toEpochMilli();
        final Wallet wallet = new Wallet(null, walletDto.name(), now, walletDto.amount(), userId);
        final Wallet savedWallet = saveWallet(wallet);
        return savedWallet.id();
    }

    @Transactional
    @PreAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, #wallet.ownerId)")
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    @SuppressWarnings("UnusedReturnValue")
    public String update(Wallet wallet) {
        Wallet walletToBeUpdated = walletRepository.getWalletById(wallet.id());
        walletRepository.updateWallet(wallet);
        return walletToBeUpdated.ownerId();
    }

    @Transactional
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    @SuppressWarnings("UnusedReturnValue")
    public String delete(UUID walletId) {
        Wallet walletToBeDeleted = walletRepository.getWalletById(walletId);
        walletRepository.deleteWallet(walletId);
        return walletToBeDeleted.ownerId();
    }

    private Wallet saveWallet(Wallet wallet) {
        return walletRepository.saveWallet(wallet);
    }
}
