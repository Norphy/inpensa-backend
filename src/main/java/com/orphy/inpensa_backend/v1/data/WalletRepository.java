package com.orphy.inpensa_backend.v1.data;

import com.orphy.inpensa_backend.v1.model.Wallet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository {

    public List<Wallet> getAllWallets();

    public List<Wallet> getAllWalletsByUser(String userId);

    public Wallet getWalletById(UUID id);

//    public Wallet getWalletByIdAndUser(UUID id, String userId);

    public Wallet saveWallet(Wallet transaction);

    public void updateWallet(Wallet transaction);

    public void deleteWallet(UUID id);
}
