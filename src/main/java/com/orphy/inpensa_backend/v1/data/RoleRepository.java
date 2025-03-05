package com.orphy.inpensa_backend.v1.data;

import com.orphy.inpensa_backend.v1.model.RoleInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository {
    public List<RoleInfo> getAllRoles();

    public RoleInfo getRoleById(UUID id);

    public RoleInfo getRoleByValue(String value);

    public RoleInfo saveRole(RoleInfo roleInfo);

    public void updateRole(RoleInfo roleInfo);

    public void deleteRole(UUID roleId);

}
