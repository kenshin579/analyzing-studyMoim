package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.settings.ProfileForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public Account processSignUp(@Valid SignUpForm signUpForm) {
        Account newAccount = saveAccountToDB(signUpForm);
        newAccount.generateEmailCheckToken();
        makeMailThenSend(newAccount);
        return newAccount;
    }

    private Account saveAccountToDB(@Valid SignUpForm signUpForm) {
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

    @Transactional(readOnly = true)
    public void makeMailThenSend(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디모임, 회원가입 인증");
        mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken() + "&email="+newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token );
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null){
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if(account == null){
            throw new UsernameNotFoundException(emailOrNickname);
        }
        //principle에 해당하는 객체를 넘기면 된다.
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.CompleteSignUp();
        login(account);
    }

    public void updateProfile(Account account, ProfileForm profileForm) {
        account.setBio(profileForm.getBio());
        account.setLivingArea(profileForm.getLivingArea());
        account.setOccupation(profileForm.getOccupation());
        account.setPersonalUrl(profileForm.getPersonalUrl());
        accountRepository.save(account);
    }
}

