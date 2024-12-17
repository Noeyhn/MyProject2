package com.github.cupangclone.service.security;

import com.github.cupangclone.repository.redis.RedisRepository;
import com.github.cupangclone.web.exceptions.NullPointException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {

    private final RedisRepository redisRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String token = getTokenFromRequest(request);
        if ( token != null ) {
            redisRepository.saveRedisToToken(token);
            SecurityContextHolder.clearContext();
        } else {
            throw new NullPointException("유효하지 않은 토큰입니다.");
        }

    }

    private String getTokenFromRequest(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if ( token != null && token.startsWith("Bearer ") ) {
            return token.substring(7);
        } else {
            return null;
        }

    }
}
