package com.studyolle.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class loginForm {
    @NotBlank
    private String id;
    @NotBlank
    private String password;
}
