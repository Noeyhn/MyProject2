package com.github.cupangclone.service.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String message = null;
        int statusCode = HttpServletResponse.SC_UNAUTHORIZED;

        if (exception instanceof UsernameNotFoundException) {
            message = "존재하지 않는 계정입니다.";
            statusCode = HttpServletResponse.SC_NOT_FOUND;
        } else if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
            message = "이메일 또는 비밀번호가 맞지 않습니다. 다시 한번 확인바랍니다.";
            statusCode = HttpServletResponse.SC_UNAUTHORIZED;
        } else {
            message = "알 수 없는 이유로 로그인에 실패하였습니다. 관리자에게 문의하세요.";
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        String encodingMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.setStatus(statusCode);
        response.getWriter().write(encodingMessage);
        response.getWriter().flush();
//        super.onAuthenticationFailure(request, response, exception);

    }
}
