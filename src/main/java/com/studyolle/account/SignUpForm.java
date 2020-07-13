package com.studyolle.account;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter //-> 이거 안하면, signUpForm테스트 에러남... null값이 들어감...
//@ToString
@NoArgsConstructor
//@AllArgsConstructor
//@RequiredArgsConstructor -> error : Constructor SignUpForm이 이미 존재한다.
public class SignUpForm {
    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;
}
