package com.github.cupangclone.web.controller;

import com.github.cupangclone.service.AuthService;
import com.github.cupangclone.web.dto.userInfo.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/check")
    public UserInfoResponse CheckInfoUser(HttpServletRequest request, HttpServletResponse response) {
        return authService.checkInfoUser(request, response);
    }

}
