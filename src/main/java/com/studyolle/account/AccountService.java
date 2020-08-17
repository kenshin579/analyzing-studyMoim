package com.studyolle.account;

import com.studyolle.config.AppProperties;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.mail.EmailMessage;
import com.studyolle.mail.EmailService;
import com.studyolle.settings.form.NotificationsForm;
import com.studyolle.settings.form.PasswordForm;
import com.studyolle.settings.form.ProfileForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    public Account processSignUp(@Valid SignUpForm signUpForm) {
        Account newAccount = saveAccountToDB(signUpForm);
        makeMailThenSend(newAccount);
        return newAccount;
    }

    private Account saveAccountToDB(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public void makeMailThenSend(Account newAccount) {
        Context context = new Context();
        context.setVariable("link","/check-email-token?token="+ newAccount.getEmailCheckToken() + "&email="+newAccount.getEmail());
        context.setVariable("message","스터디 올래 회원가입 이메일 인증메일입니다.");
        context.setVariable("nickname",newAccount.getNickname());
        context.setVariable("serviceName","이메일 인증하기");
        context.setVariable("host",appProperties.getHost());
        String process = templateEngine.process("mail/mailMessage", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디모임, 회원가입 인증")
                .message(process)
                .build();
        emailService.sendEmail(emailMessage);
    }

    @Transactional(readOnly = true)
    public void emailLoginSend(Account account){
        Context context = new Context();
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link","/login-by-email?token="+account.getEmailCheckToken()+"&email="+account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("serviceName","스터디모임 이메일로 로그인하기");
        String process = templateEngine.process("mail/mailMessage", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디모임 이메일로 로그인하기")
                .message(process)
                .build();
        emailService.sendEmail(emailMessage);
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
        modelMapper.map(profileForm, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, NotificationsForm notificationsForm) {
        modelMapper.map(notificationsForm, account);
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

    public void removeTag(Account account, Tag removeTag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(account1 -> account1.getTags().remove(removeTag));
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(account1 -> account1.getZone().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(account1 -> account1.getZone().remove(zone));
    }

    public Set<Zone> getZone(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZone();
    }
}

