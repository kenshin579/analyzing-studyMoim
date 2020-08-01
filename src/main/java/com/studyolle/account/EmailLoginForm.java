package com.studyolle.account;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailLoginForm {
    @NotBlank
    @Email
    private String email;
}
