package com.github.cupangclone.web.dto.signUp;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SignUpRequest {

    private String email;
    private String phoneNum;
    private String password;
    private String address;
    private String gender;
    private boolean seller;

}
