package com.studyolle.modules.account.validator;

import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.form.AccountForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class AccountValidator implements Validator {
    private final AccountRepository accountRepository;
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        AccountForm accountForm = (AccountForm)object;
        if(accountRepository.existsByNickname(accountForm.getNewNickname())){
            errors.rejectValue("newNickname","invalid.newNickname", new Object[]{accountForm.getNewNickname()},"존재하는 닉네임입니다.");
        }
    }
}
