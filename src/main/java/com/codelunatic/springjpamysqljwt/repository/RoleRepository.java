package com.codelunatic.springjpamysqljwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codelunatic.springjpamysqljwt.model.Role;
import com.codelunatic.springjpamysqljwt.model.RoleName;
import com.codelunatic.springjpamysqljwt.model.UserRole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}