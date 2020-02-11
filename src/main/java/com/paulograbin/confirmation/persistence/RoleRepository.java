package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.Role;
import com.paulograbin.confirmation.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName roleName);

}