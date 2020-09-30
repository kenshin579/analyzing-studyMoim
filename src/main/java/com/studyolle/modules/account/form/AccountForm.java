package com.studyolle.modules.account.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
public class AccountForm {
    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp ="^[ㄱ-ㅎ가-핳a-zA-Z0-9_-]{3,20}$")
    private String newNickname;

    public AccountForm(String nickname) {
        this.newNickname = nickname;
    }
}
