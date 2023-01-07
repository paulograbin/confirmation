package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.RoleRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;


@Service
public class RoleService {

//    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Resource
    private RoleRepository roleRepository;


    public long fetchRoleCount() {
        return roleRepository.count();
    }

    public Role getByName(RoleName role) {
        return roleRepository.findByName(role)
                .orElseThrow(() -> new NotFoundException(format("Role %s not found", role.toString())));
    }

    public Role getUserRole() {
        Optional<Role> adminOptional = roleRepository.findByName(RoleName.ROLE_USER);

        return adminOptional.orElseThrow(() -> new NotFoundException("Role ADMIN not found!"));
    }

    public Role getAdminRole() {
        Optional<Role> adminOptional = roleRepository.findByName(RoleName.ROLE_ADMIN);

        return adminOptional.orElseThrow(() -> new NotFoundException("Role ADMIN not found!"));
    }
}
