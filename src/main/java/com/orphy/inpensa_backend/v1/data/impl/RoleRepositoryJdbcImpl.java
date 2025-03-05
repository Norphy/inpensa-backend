package com.orphy.inpensa_backend.v1.data.impl;

import com.orphy.inpensa_backend.v1.data.RoleRepository;
import com.orphy.inpensa_backend.v1.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.v1.exceptions.UnExpectedException;
import com.orphy.inpensa_backend.v1.model.Role;
import com.orphy.inpensa_backend.v1.model.RoleInfo;
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
public class RoleRepositoryJdbcImpl implements RoleRepository {

    private final JdbcTemplate jdbcTemplate;
    public RoleRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RoleInfo> getAllRoles() {
        final String SQL_GET_ALL_ROLES = "SELECT * FROM T_ROLE;";
        return  jdbcTemplate.query(SQL_GET_ALL_ROLES, ROLE_ROW_MAPPER);
    }

    @Override
    public RoleInfo getRoleById(UUID id) {
        final String SQL_GET_ALL_ROLES_BY_ID = "SELECT * FROM T_ROLE WHERE ID = ?;";
        List<RoleInfo> listOfRoles = jdbcTemplate.query(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_ROLES_BY_ID);
            String idStr = id.toString();
            ps.setString(1, idStr);
            return ps;
        }, ROLE_ROW_MAPPER);
        return listOfRoles.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("RoleInfo not found"));
    }

    @Override
    public RoleInfo getRoleByValue(String value) {
        final String SQL_GET_ALL_ROLES_BY_VALUE = "SELECT * FROM T_ROLE WHERE ROLE = ?;";
        List<RoleInfo> listOfRoles = jdbcTemplate.query(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_ROLES_BY_VALUE);
            ps.setString(1, value);
            return ps;
        }, ROLE_ROW_MAPPER);
        return listOfRoles.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("RoleInfo not found"));
    }

    @Override
    public RoleInfo saveRole(RoleInfo roleInfo) {
        final String SQL_SAVE_ROLE = "INSERT INTO T_ROLE (ROLE, DATE_CREATED, CREATED_BY) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_SAVE_ROLE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, roleInfo.role().getRoleValue());
            ps.setLong(2, roleInfo.dateCreated());
            ps.setString(3, roleInfo.createdBy());
            return ps;
        }, keyHolder);

        final String idStr = keyHolder.getKeys().values()
                .stream().findAny()
                .orElseThrow(() -> new UnExpectedException("Error getting generated Id.")).toString();
        final UUID id = UUID.fromString(idStr);
        return roleInfo.copyWithId(id);
    }

    @Override
    public void updateRole(RoleInfo role) {
        final String SQL_UPDATE_ROLE = "UPDATE T_ROLE SET ROLE = ?, DATE_CREATED = ?, CREATED_BY = ? WHERE ID = ?;";
        String idStr = role.id().toString();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_ROLE);
            ps.setString(1, role.role().getRoleValue());
            ps.setLong(2, role.dateCreated());
            ps.setString(3, role.createdBy());
            ps.setString(4, idStr);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error updating Role with id: " + idStr);
        }
    }

    @Override
    public void deleteRole(UUID roleId) {
        final String SQL_DELETE_ROLE = "DELETE FROM T_ROLE WHERE ID = ?;";
        String idStr = roleId.toString();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_ROLE);
            ps.setString(1, idStr);
            return ps;
        });

        if(rowsAffected != 1) {
            throw new UnExpectedException("Error deleting Role with id: " + idStr);
        }
    }

    private final RowMapper<RoleInfo> ROLE_ROW_MAPPER =
            (rs, rowNum) -> {
                String idStr = rs.getString("ID");
                String role = rs.getString("ROLE");
                long dateCreated = rs.getLong("DATE_CREATED");
                String createdBy = rs.getString("CREATED_BY");
                UUID id = UUID.fromString(idStr);
                //TODO create alert for this error
                return new RoleInfo(id, Role.valueOfRole(role, () -> new RuntimeException("""
            This error should not have occurred.
            It indicates that role value was entered incorrectly in database.""")), dateCreated, createdBy);
            };
}
