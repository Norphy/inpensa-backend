package com.orphy.inpensa_backend.v1.service;

import com.orphy.inpensa_backend.v1.data.RoleRepository;
import com.orphy.inpensa_backend.v1.model.Role;
import com.orphy.inpensa_backend.v1.model.RoleInfo;
import com.orphy.inpensa_backend.v1.util.security.annotations.IsAdminRead;
import com.orphy.inpensa_backend.v1.util.security.annotations.IsAdminWrite;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @IsAdminRead
    public List<RoleInfo> getAllRoles() {
        return roleRepository.getAllRoles();
    }

    @IsAdminRead
    public RoleInfo getRoleById(UUID roleId) {
        return roleRepository.getRoleById(roleId);
    }


    @IsAdminRead
    public RoleInfo getRoleByValue(String roleValue) {
        return roleRepository.getRoleByValue(roleValue);
    }

    //TODO is this required
    @IsAdminWrite
    public UUID saveRole(String roleValue) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String userId = auth.getName();
        long now = Instant.now().toEpochMilli();

        RoleInfo toBeSaved = new RoleInfo(null, Role.valueOfRole(roleValue, () -> new DataIntegrityViolationException("Role Value is incorrect.")), now, userId);
        RoleInfo savedRole = roleRepository.saveRole(toBeSaved);
        return savedRole.id();
    }

    //TODO is this required
    @IsAdminWrite
    public void updateRole(RoleInfo roleInfo) {
        roleRepository.updateRole(roleInfo);
    }

    //TODO is this required
    @IsAdminWrite
    public void deleteRole(UUID roleId) {
        roleRepository.deleteRole(roleId);
    }

}
