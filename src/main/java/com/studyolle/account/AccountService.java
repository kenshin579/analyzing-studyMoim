package com.studyolle.account;

import com.studyolle.domain.Account;
import com.sun.tools.javac.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account processSignUp(@Valid SignUpForm signUpForm) {
        Account newAccount = saveAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        makeMailThenSend(newAccount);
        return newAccount;
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

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                account.getNickname(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token );

    }
}
