package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public void processSignUp(@Valid SignUpForm signUpForm) {
        Account newAccount = saveAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        makeMailThenSend(newAccount);
    }

    private Account saveAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .alarmApplyResultToWeb(true)
                .alarmStudyCreationToWeb(true)
                .alarmUpdateInfoToWeb(true)
                .build();
        return accountRepository.save(account);
    }

    private void makeMailThenSend(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디모임, 회원가입 인증");
        mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken() + "&email="+newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }
}
