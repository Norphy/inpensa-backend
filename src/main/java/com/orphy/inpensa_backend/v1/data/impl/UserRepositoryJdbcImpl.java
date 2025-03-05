package com.orphy.inpensa_backend.v1.data.impl;

import com.orphy.inpensa_backend.v1.data.UserRepository;
import com.orphy.inpensa_backend.v1.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.v1.exceptions.UnExpectedException;
import com.orphy.inpensa_backend.v1.model.Role;
import com.orphy.inpensa_backend.v1.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserRepositoryJdbcImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {

        final String SQL_GET_ALL_USERS = """
                SELECT u.ID, u.EMAIL, u.DATE_CREATED, r.ROLE FROM T_USER u
                LEFT JOIN T_ROLE r ON u.ROLE_ID = r.ID;
                """;
        return  jdbcTemplate.query(SQL_GET_ALL_USERS, USER_ROW_MAPPER);
    }

    @Override
    public User getUserById(String userId) {
        final String SQL_GET_USER_ID = """
                SELECT u.ID, u.EMAIL, u.DATE_CREATED, r.ROLE FROM T_USER u
                LEFT JOIN T_ROLE r ON u.ROLE_ID = r.ID
                WHERE u.ID = ?;
                """;
        List<User> listOfUsers = jdbcTemplate.query(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_GET_USER_ID);
            ps.setString(1, userId);
            return ps;
        }, USER_ROW_MAPPER);
        return  listOfUsers.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        final String SQL_GET_USER_ID = """
                SELECT u.ID, u.EMAIL, u.DATE_CREATED, r.ROLE FROM T_USER u
                LEFT JOIN T_ROLE r ON u.ROLE_ID = r.ID
                WHERE u.EMAIL = ?;
                """;
        List<User> listOfUsers = jdbcTemplate.query(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_GET_USER_ID);
            ps.setString(1, email);
            return ps;
        }, USER_ROW_MAPPER);
        return  listOfUsers.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User saveUser(User user) {
        final String SQL_SAVE_USER = """
                INSERT INTO T_USER (ID, EMAIL, DATE_CREATED, ROLE_ID)
                VALUES (?, ?, ?, (SELECT T_ROLE.ID WHERE T_ROLE.ROLE = ?));
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_SAVE_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.id());
            ps.setString(2, user.email());
            ps.setLong(3, user.dateCreated());
            ps.setString(4, user.role().getRoleValue());
            return ps;
        }, keyHolder);
        final String id = keyHolder.getKeys().values()
                .stream().findAny()
                .orElseThrow(() -> new UnExpectedException("Error getting generated Id.")).toString();
        return user.copyWithId(id);
    }

    @Override
    public void updateUser(User user) {
        final String SQL_UPDATE_USER = """
                UPDATE T_USER
                SET EMAIL = ?, DATE_CREATED = ?, ROLE = (SELECT T_ROLE.ID WHERE T_ROLE.ROLE = ?) WHERE ID = ?;
                """;
        String id = user.id();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_USER);
            ps.setString(1, user.email());
            ps.setLong(2, user.dateCreated());
            ps.setString(3, user.role().getRoleValue());
            ps.setString(4, id);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error updating User with id: " + id);
        }
    }

    @Override
    public void deleteUser(String userId) {
        final String SQL_DELETE_USER = "DELETE FROM T_USER WHERE ID = ?;";
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_USER)     ;
            ps.setString(1, userId);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error deleting User with id: " + userId);
        }
    }

    private final RowMapper<User> USER_ROW_MAPPER =
            (rs, rowNum) -> {
                String id = rs.getString("ID");
                String email = rs.getString("EMAIL");
                long dateCreated = rs.getLong("DATE_CREATED");
                String role = rs.getString("ROLE");
                return new User(id, email, dateCreated, Role.valueOfRole(role, () -> new RuntimeException("""
                This error should not occur.
                It indicates that Role value was incorrectly saved in database.
                """)));
            };
}
