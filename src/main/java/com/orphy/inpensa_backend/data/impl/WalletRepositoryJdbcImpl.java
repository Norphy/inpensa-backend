package com.orphy.inpensa_backend.data.impl;

import com.orphy.inpensa_backend.data.WalletRepository;
import com.orphy.inpensa_backend.exceptions.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.exceptions.data.UnExpectedException;
import com.orphy.inpensa_backend.model.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

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

    @Override
    public Wallet getWalletByIdAndUser(UUID id, String userId) {
        logger.debug("Get Wallet by Id and by user. Id: {} User Id: {}", id, userId);
        String SQL_GET_WALLET_BY_ID_AND_BY_USER = "SELECT * FROM T_WALLET WHERE ID = ? AND WHERE OWNER_ID = ?;";
        List<Wallet> wallets  = jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_GET_WALLET_BY_ID_AND_BY_USER);
            String idStr = id.toString();
            ps.setString(1, idStr);
            ps.setString(2, userId);
            return ps;
        }, WALLET_ROW_MAPPER);
        return wallets.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Wallet not found. Id: " + id));
    }

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
        logger.debug("Update wallet. Wallet id: {}", wallet.id());
        String SQL_SAVE_NEW_WALLET = "INSERT INTO T_WALLET (ID, NAME, DATE_CREATED, AMOUNT, OWNER_ID) VALUES (?, ?, ?, ?, ?);";
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_SAVE_NEW_WALLET, Statement.RETURN_GENERATED_KEYS);
            String idStr = wallet.id().toString();
            ps.setString(1, idStr);
            ps.setString(2, wallet.name());
            ps.setLong(3, wallet.dateCreated());
            ps.setLong(4, wallet.amount());
            ps.setString(5, wallet.ownerId());
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error updating wallet with id: " + wallet.id());
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
