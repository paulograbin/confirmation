package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.Role;
import com.paulograbin.confirmation.persistence.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class RoleService {
    
    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    
    @Resource
    private RoleRepository roleRepository;


    public Role save(Role roleToSave) {
        return roleRepository.save(roleToSave);
    }
}
