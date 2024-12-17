package com.github.cupangclone.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private String secretKey;

    // 토큰 만료시간 1시간
    private static final long accessTokenValid = 1000 * 60 * 60;

    // 토큰 만료시간 7일
//    private static final long refreshTokenValid = 1000 * 60 * 60 * 24 * 7;

    @PostConstruct
    protected void init() {
        this.secretKey = Base64
                .getEncoder().encodeToString(secret.getBytes());
    }

    public String createAccessToken(String email, List<String> roles) {

        Date now = new Date();
        return Jwts
                .builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .claim("roles", roles)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValid))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }

//    public String createRefreshToken(String email, List<String> roles) {
//
//        Date now = new Date();
//        Claims claims = Jwts.claims()
//                .setSubject("refreshToken");
//        return Jwts
//                .builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + refreshTokenValid))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//
//    }
//
//    public String refreshToken(String refreshToken) {
//        try {
//            Authentication authentication = getAuthentication(refreshToken);
//        }
//    }

    public boolean validateToken(String jwtToken) {

        try {
            Claims claims = getClaims(jwtToken);
            Date now = new Date();
            return claims.getExpiration().after(now);
        } catch (Exception e) {
            return false;
        }

    }

    public Authentication getAuthentication(String jwtToken) {

        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody();
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        if ( username == null || username.isEmpty() ) {
            throw new IllegalArgumentException("사용자 정보가 존재하지 않습니다.");
        }

        if ( roles == null || roles.isEmpty() ) {
            throw new IllegalArgumentException("사용자 권한을 찾을 수 없습니다.");
        }


//        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new User(
                username, "", roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())),
                jwtToken,
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );

    }

    private Claims getClaims(String jwtToken) {

        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken)
                .getBody();


    }

    public String getUsername(String jwtToken) {

        return getClaims(jwtToken).getSubject();

    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;

    }

    public void setAuthentication(String token, HttpServletRequest request) {

        if (token != null && validateToken(token)) {
            Authentication auth = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

    }

    public long getRemainingTime(String jwtToken) {

        Claims claims = getClaims(jwtToken);
        Date now = new Date();

        return claims.getExpiration().getTime() - now.getTime();

    }
}
