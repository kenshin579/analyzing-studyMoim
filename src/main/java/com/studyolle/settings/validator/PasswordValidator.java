package com.studyolle.settings.validator;

import com.studyolle.account.AccountRepository;
import com.studyolle.settings.form.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class PasswordValidator implements Validator {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        PasswordForm passwordForm = (PasswordForm)object;
        if(!passwordForm.getNewPassword().equals(passwordForm.getCheckNewPassword())){
            errors.rejectValue("checkNewPassword","invalid.checkNewPassword", new Object[]{passwordForm.getCheckNewPassword()}, "비밀번호가 다릅니다.");
        }
        /*if(accountRepository.findByNickname(passwordForm.getNickname()).getPassword() == passwordEncoder.encode(passwordForm.getNewPassword())){
            errors.rejectValue("checkNewPassword","invalid.checkNewPassword", new Object[]{passwordForm.getCheckNewPassword()}, "이미 사용 중인 비밀번호입니다.");
        }*/

    }
}
