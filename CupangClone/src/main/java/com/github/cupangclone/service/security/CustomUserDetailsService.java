package com.github.cupangclone.service.security;

import com.github.cupangclone.repository.userDetails.CustomUserDetails;
import com.github.cupangclone.repository.roles.Roles;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import com.github.cupangclone.repository.userPrincipal.UserPrincipalRepository;
import com.github.cupangclone.repository.userPrincipalRoles.UserPrincipalRoles;
import com.github.cupangclone.repository.userPrincipalRoles.UserPrincipalRolesRepository;
import com.github.cupangclone.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserPrincipalRepository userPrincipalRepository;
    private final UserPrincipalRolesRepository userPrincipalRolesRepository;

    @Override
    @Transactional(transactionManager = "tmJpa1")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserPrincipal userPrincipal = userPrincipalRepository
                .findByEmail(email)
                .orElseThrow( () -> new NotFoundException(email + "유저를 찾을 수 없습니다."));

        Optional<UserPrincipalRoles> userPrincipalRoles = userPrincipalRolesRepository
                .findByUserPrincipal_UserPrincipalId(userPrincipal
                        .getUserPrincipalId());

        return CustomUserDetails
                .builder()
                .userPrincipalId(userPrincipal.getUserPrincipalId())
                .email(userPrincipal.getEmail())
                .password(userPrincipal.getPassword())
                .authorities(userPrincipalRoles
                        .stream()
                        .map(UserPrincipalRoles::getRoles)
                        .map(Roles::getRoleName)
                        .collect(Collectors.toList()))
                .build();
    }

}
