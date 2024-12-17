package com.github.cupangclone.repository.userPrincipal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPrincipalRepository extends JpaRepository<UserPrincipal, Long> {

    @Query
    Optional<UserPrincipal> findByEmail(String email);

    boolean existsByEmail(String email);

}
