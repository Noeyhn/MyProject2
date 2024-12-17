package com.github.cupangclone.web.dto.userInfo;

import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserInfoResponse {

    private String email;
    private String phoneNum;
    private String address;
    private String gender;

    public static UserInfoResponse formUser(UserPrincipal userPrincipal) {

        return UserInfoResponse
                .builder()
                .email(userPrincipal.getEmail())
                .phoneNum(userPrincipal.getPhoneNum())
                .address(userPrincipal.getAddress())
                .gender(userPrincipal.getGender())
                .build();

    }

}
