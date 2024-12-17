package com.github.cupangclone.service;

import com.github.cupangclone.config.security.JwtTokenProvider;
import com.github.cupangclone.repository.items.Items;
import com.github.cupangclone.repository.redis.RedisRepository;
import com.github.cupangclone.repository.roles.Roles;
import com.github.cupangclone.repository.roles.RolesRepository;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import com.github.cupangclone.repository.userPrincipal.UserPrincipalRepository;
import com.github.cupangclone.repository.userPrincipalRoles.UserPrincipalRoles;
import com.github.cupangclone.repository.userPrincipalRoles.UserPrincipalRolesRepository;
import com.github.cupangclone.web.dto.login.LoginRequest;
import com.github.cupangclone.web.dto.signUp.SignUpRequest;
import com.github.cupangclone.web.dto.userInfo.UserInfoResponse;
import com.github.cupangclone.web.exceptions.NotAcceptException;
import com.github.cupangclone.web.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserPrincipalRepository userPrincipalRepository;
    private final UserPrincipalRolesRepository userPrincipalRolesRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepository redisRepository;

    @Transactional(transactionManager = "tmJpa1")
    public boolean createUser(HttpServletResponse response, SignUpRequest signUpRequest) {

        if (userPrincipalRepository.existsByEmail(signUpRequest.getEmail())) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;

        } else {

            UserPrincipal user = UserPrincipal
                    .builder()
                    .email(signUpRequest.getEmail())
                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                    .address(signUpRequest.getAddress())
                    .phoneNum(signUpRequest.getPhoneNum())
                    .gender(signUpRequest.getGender())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build();

            Roles roles;

            if ( signUpRequest.isSeller() ) {
                roles = rolesRepository.findByRoleName("ROLE_SELLER");
            } else {
                roles = rolesRepository.findByRoleName("ROLE_USER");
            }

            UserPrincipalRoles userRoles = UserPrincipalRoles
                    .builder()
                    .userPrincipal(user)
                    .roles(roles)
                    .build();

            userPrincipalRepository.save(user);
            userPrincipalRolesRepository.save(userRoles);

            return true;
        }

    }

    public String loginUser(HttpServletResponse response, LoginRequest loginRequest) {

        try {

            UserPrincipal user = userPrincipalRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow();

            if ( user.getIsDeleted() ) {
                throw new NotFoundException(response, "사용자를 찾을 수 없습니다.");
            }

            Optional<UserPrincipalRoles> userRoles = userPrincipalRolesRepository
                    .findByUserPrincipal_UserPrincipalId(user.getUserPrincipalId());

            Authentication auth = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getEmail(), loginRequest.getPassword()
                            )
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

            List<String> roles = userRoles
                    .stream()
                    .map(UserPrincipalRoles::getRoles)
                    .map(Roles::getRoleName)
                    .toList();

            return jwtTokenProvider.createAccessToken(loginRequest.getEmail(), roles);

        } catch (UsernameNotFoundException | NotFoundException e ) {
            throw new NotFoundException(response, "사용자를 찾을 수 없습니다.");
        } catch ( BadCredentialsException e ) {
            throw new BadCredentialsException("자격 증명이 잘못되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 오류가 발생하였습니다.");
        }

    }

    @Transactional(transactionManager = "tmJpa1")
    public boolean resignUser(HttpServletRequest request, String originPassword) {

        String token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getUsername(token);

        try {
            UserPrincipal user = userPrincipalRepository.findByEmail(email)
                    .orElseThrow();

            String password = passwordEncoder.encode(originPassword);

            if ( password == null || !Objects.equals(password, user.getPassword())) {
                return false;
            }

            user.setIsDeleted(true);

            userPrincipalRepository.save(user);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 오류가 발생하였습니다.");
        }
    }

    public UserInfoResponse checkInfoUser(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtTokenProvider.resolveToken(request);

        if ( redisRepository.findRedisToToken(token) ) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new NotAcceptException("잘못된 접근입니다.");

        } else {

            String email = jwtTokenProvider.getUsername(token);

            UserPrincipal user = userPrincipalRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
            UserPrincipalRoles userRoles = userPrincipalRolesRepository.findById(user.getUserPrincipalId())
                    .orElseThrow(() -> new NotFoundException("유저 정보에 오류가 있습니다."));

            return UserInfoResponse.formUser(user);

        }
    }
}
