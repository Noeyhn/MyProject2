package com.github.cupangclone.repository.userPrincipalRoles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPrincipalRolesRepository extends JpaRepository<UserPrincipalRoles, Long> {
    Optional<UserPrincipalRoles> findByUserPrincipal_UserPrincipalId(Long userPrincipalId);
}
