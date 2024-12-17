package com.github.cupangclone.web.controller;

import com.github.cupangclone.service.AuthService;
import com.github.cupangclone.web.dto.login.LoginRequest;
import com.github.cupangclone.web.dto.signUp.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class SignController {

    private final AuthService authService;

    @PostMapping("/register")
    public String RegisterUser(@RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        signUpRequest.setSeller(false);
        boolean isSuccess = authService.createUser(response, signUpRequest);
        return isSuccess ? "회원가입에 성공하였습니다." : "회원가입에 실패하였습니다.";
    }

    @PostMapping("/seller/register")
    public String RegisterSeller(@RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        signUpRequest.setSeller(true);
        boolean isSuccess = authService.createUser(response, signUpRequest);
        return isSuccess ? "판매자 회원가입에 성공하였습니다." : "판매자 회원가입에 실패하였습니다.";
    }


    @PostMapping("/login")
    public String LoginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        String token = authService.loginUser(response, loginRequest);
        response.setHeader("Authorization", "Bearer " + token);

        return "로그인에 성공하였습니다.";

    }

    @DeleteMapping("/resign")
    public ResponseEntity<String> ResignUser(@RequestParam("password") String password, HttpServletRequest request) {

        if ( authService.resignUser(request, password) ) {
            return ResponseEntity.ok("성공적으로 탈퇴되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 맞지 않습니다.");
        }

    }
}
