package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.form.NotificationsForm;
import com.studyolle.settings.form.PasswordForm;
import com.studyolle.settings.form.ProfileForm;
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
import java.util.Optional;
import java.util.Set;

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

    @Transactional(readOnly = true)
    public void emailLoginSend(Account account){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디모임 이메일로 로그인하기");
        mailMessage.setText("/login-by-email?token="+account.getEmailCheckToken()+"&email="+account.getEmail());
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

    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, NotificationsForm notificationsForm) {
        account.setAlarmApplyResultToEmail(notificationsForm.isAlarmApplyResultToEmail());
        account.setAlarmApplyResultToWeb(notificationsForm.isAlarmApplyResultToWeb());
        account.setAlarmStudyCreationToEmail(notificationsForm.isAlarmStudyCreationToEmail());
        account.setAlarmStudyCreationToWeb(notificationsForm.isAlarmStudyCreationToWeb());
        account.setAlarmUpdateInfoToEmail(notificationsForm.isAlarmUpdateInfoToEmail());
        account.setAlarmUpdateInfoToWeb(notificationsForm.isAlarmUpdateInfoToWeb());
        accountRepository.save(account);
    }

    public void updateAccount(Account account, String newNickname) {
        account.setNickname(newNickname);
        accountRepository.save(account);
        login(account);
    }

    public void sendMail(String email) {
        Account account = accountRepository.findByEmail(email);
        account.generateEmailCheckToken();
        emailLoginSend(account);
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    public void emailLogin(Account account) {
        login(account);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(account1 -> account1.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, String removeTitle) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(account1 -> account1.getTags().remove(removeTitle));
    }
}

