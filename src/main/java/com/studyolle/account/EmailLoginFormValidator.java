package com.studyolle.account;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class EmailLoginFormValidator implements Validator {
    private final AccountRepository accountRepository;
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EmailLoginForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        EmailLoginForm emailLoginForm = (EmailLoginForm)object;
        if(!accountRepository.existsByEmail(emailLoginForm.getEmail())){
            errors.rejectValue("email","invalid.email", new Object[]{emailLoginForm.getEmail()},"등록되지 않은 이메일입니다.");
        }
    }
}
