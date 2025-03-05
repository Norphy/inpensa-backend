package com.orphy.inpensa_backend.v1.data.impl;

import com.orphy.inpensa_backend.v1.data.WalletRepository;
import com.orphy.inpensa_backend.v1.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.v1.exceptions.UnExpectedException;
import com.orphy.inpensa_backend.v1.model.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@Repository
public class WalletRepositoryJdbcImpl implements WalletRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    public WalletRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Wallet> getAllWallets() {

        logger.debug("Get all wallets.");
        String SQL_GET_ALL_WALLETS_BY_USER = "SELECT * FROM T_WALLET WHERE OWNER_ID = ?;";
        return jdbcTemplate.query(SQL_GET_ALL_WALLETS_BY_USER, WALLET_ROW_MAPPER);
    }

    @Override
    public List<Wallet> getAllWalletsByUser(String userId) {
        logger.debug("Get all wallets by user. User Id: {}", userId);
        String SQL_GET_ALL_WALLETS_BY_USER = "SELECT * FROM T_WALLET WHERE OWNER_ID = ?;";
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_ALL_WALLETS_BY_USER);
            ps.setString(1, userId);
            return ps;
        }, WALLET_ROW_MAPPER);
    }

    @Override
    public Wallet getWalletById(UUID id) {
        logger.debug("Get Wallet by id. Wallet Id: {}", id);
        String SQL_GET_WALLET_BY_ID_AND_BY_USER = "SELECT * FROM T_WALLET WHERE ID = ?;";
        List<Wallet> wallets  = jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_WALLET_BY_ID_AND_BY_USER);
            String idStr = id.toString();
            ps.setString(1, idStr);
            return ps;
        }, WALLET_ROW_MAPPER);
        return wallets.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Wallet not found. Id: " + id));
    }

//    @Override
//    public Wallet getWalletByIdAndUser(UUID id, String userId) {
//        logger.debug("Get Wallet by Id and by user. Id: {} User Id: {}", id, userId);
//        String SQL_GET_WALLET_BY_ID_AND_BY_USER = "SELECT * FROM T_WALLET WHERE ID = ? AND WHERE OWNER_ID = ?;";
//        List<Wallet> wallets  = jdbcTemplate.query(con -> {
//            PreparedStatement ps = con.prepareStatement(SQL_GET_WALLET_BY_ID_AND_BY_USER);
//            String idStr = id.toString();
//            ps.setString(1, idStr);
//            ps.setString(2, userId);
//            return ps;
//        }, WALLET_ROW_MAPPER);
//        return wallets.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Wallet not found. Id: " + id));
//    }

    @Override
    public Wallet saveWallet(Wallet wallet) {
        logger.debug("Save new wallet. Wallet name: {}", wallet.name());
        String SQL_SAVE_NEW_WALLET = "INSERT INTO T_WALLET (NAME, DATE_CREATED, AMOUNT, OWNER_ID) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_SAVE_NEW_WALLET, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, wallet.name());
            ps.setLong(2, wallet.dateCreated());
            ps.setLong(3, wallet.amount());
            ps.setString(4, wallet.ownerId());
            return ps;
        }, keyHolder);

        final String idStr = keyHolder.getKeys().values()
                .stream().findAny()
                .orElseThrow(() -> new UnExpectedException("Error getting generated Id.")).toString();
        final UUID id = UUID.fromString(idStr);
        return wallet.copyWithId(id);
    }

    @Override
    public void updateWallet(Wallet wallet) {
        final String SQL_UPDATE_WALLET = """
                UPDATE T_WALLET
                SET NAME = ?, DATE_CREATED = ?, AMOUNT = ?, OWNER_ID = ? WHERE ID = ?;
                """;
        String idStr = wallet.id().toString();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_WALLET);
            ps.setString(1, wallet.name());
            ps.setLong(2, wallet.dateCreated());
            ps.setString(3, wallet.ownerId());
            ps.setString(4, idStr);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error updating Role with id: " + idStr);
        }
    }

    @Override
    public void deleteWallet(UUID id) {

        logger.debug("Delete wallet. Wallet id: {}", id);
        String SQL_DELETE_WALLET = "DELETE FROM T_WALLET WHERE ID = ?;";
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_WALLET);
            String idStr = id.toString();
            ps.setString(1, idStr);
            return ps;
        });
        if(rowsAffected != 1) {
            throw new UnExpectedException("Error deleting wallet with id: " + id);
        }
    }

    private final RowMapper<Wallet> WALLET_ROW_MAPPER =
            (rs, rowNum) -> {
                String idStr = rs.getString("ID");
                String name = rs.getString("NAME");
                long dateCreated = rs.getLong("DATE_CREATED");
                long amount = rs.getLong("AMOUNT");
                String ownerIdStr = rs.getString("OWNER_ID");
                UUID id = UUID.fromString(idStr);
                return new Wallet(id, name, dateCreated, amount, ownerIdStr);
            };
}
