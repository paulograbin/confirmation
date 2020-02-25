package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Optional;

import static java.lang.String.format;


@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Resource
    private RoleRepository roleRepository;


    public long fetchRoleCount() {
        return roleRepository.count();
    }

    public Role save(Role roleToSave) {
        return roleRepository.save(roleToSave);
    }

    public Role getByName(RoleName role) {
        return roleRepository.findByName(role)
                .orElseThrow(() -> new NotFoundException(format("Role %s not found", role.toString())));
    }

    public Role getAdmin() {
        Optional<Role> adminOptional = roleRepository.findByName(RoleName.ROLE_ADMIN);

        return adminOptional.orElseThrow(() -> new NotFoundException("Role ADMIN not found!"));
    }
}
