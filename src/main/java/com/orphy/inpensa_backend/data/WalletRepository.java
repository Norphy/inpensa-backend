package com.orphy.inpensa_backend.data;

import com.orphy.inpensa_backend.model.Transaction;
import com.orphy.inpensa_backend.model.Wallet;

import java.util.List;
import java.util.UUID;

public interface WalletRepository {

    public List<Wallet> getAllWallets();

    public List<Wallet> getAllWalletsByUser(String userId);

    public Wallet getWalletById(UUID id);

    public Wallet getWalletByIdAndUser(UUID id, String userId);

    public Wallet saveWallet(Wallet transaction);

    public void updateWallet(Wallet transaction);

    public void deleteWallet(UUID id);
}
