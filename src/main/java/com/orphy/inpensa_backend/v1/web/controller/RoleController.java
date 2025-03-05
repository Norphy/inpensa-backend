package com.orphy.inpensa_backend.v1.web.controller;

import com.orphy.inpensa_backend.v1.model.RoleInfo;
import com.orphy.inpensa_backend.v1.service.RoleService;
import com.orphy.inpensa_backend.v1.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleInfo> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RoleInfo getRoleByID(@PathVariable String id) {
        UUID uuid = Util.tryOrElse(() -> UUID.fromString(id),
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return roleService.getRoleById(uuid);
    }

    @GetMapping("/value/{value}")
    public RoleInfo getRoleByValue(@PathVariable String value) {
        return roleService.getRoleByValue(value);
    }

    //TODO remove after testing
    @PostMapping
    public void getRoleByValue(@RequestBody RoleInfo value) {
        System.out.println("RoleInfo: " + value);
    }
}
