package com.studyolle;

import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(AccountController.class)
@Transactional
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 뷰페이지가 들어가지는 지 테스트")
    @Test
    public void AccountTest() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    public void signUpForm오류테스트() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","keesun")
                .param("email","email...")
                .param("password","123456678")
                .with(csrf())) //TODONE : csrf는 지금 이 프로젝트의 Form식별코드이다. Form 테스트를 하는 이 테스트메서드에는 이 프로젝트의 Form 식별코드 설정을 추가해줘야 403에러를 방지할 수 있다.
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Form 정상 테스트")
    @Test
    public void signUpForm정상테스트() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","kekeke")
                .param("email","insookim0702@gmail.com")
                .param("password","123123123").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        //assertTrue(accountRepository.existsByEmail("insookim0702@gmail.com"));
        Account account = accountRepository.findByEmail("insookim0702@gmail.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "123123123");
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("잘못된 인증메일 확인 테스트")
    @Test
    public void 잘못된_인증메일_테스트() throws Exception {
        mockMvc.perform(get("/check-email-token").param("token","ekekek").param("email","keesun...."))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("정상 인증메일 확인 테스트")
    @Test
    public void 정상_인증메일_테스트() throws Exception {
        Account buildedAccount = Account.builder().email("insookim0702@gmail.com").nickname("nickname").password("123123123").build();
        Account savedAccount = accountRepository.save(buildedAccount);
        savedAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token",savedAccount.getEmailCheckToken())
                .param("email", savedAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated() );
    }

    @DisplayName("이메일 재인증 페이지 정상 작동 확인")
    @Test
    public void 이메일재인증요청페이지_정상() throws Exception {
        //회원가입하고..
        Account account = Account.builder().nickname("wewewew").email("asdf@email.com").password("123123123").build();
        Account savedAccount = accountRepository.save(account);

        //로그인하고..
        accountService.login(savedAccount);

        //테스트
        mockMvc.perform(get("/recheck-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/recheck-email"))
                .andExpect(authenticated());
    }

    @DisplayName("이메일 재인증 요청_정상")
    @Test
    public void 이메일_재인증_요청() throws Exception {
        Account account = Account.builder().nickname("qweqweqwe").email("asdf@email.com").password("123123123").build();
        Account saveAccount = accountRepository.save(account);

        //로그인
        saveAccount.generateEmailCheckToken();
        accountService.login(saveAccount);

        //테스트
        mockMvc.perform(get("/request-emailValidateToken"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("oneHourError"))
                .andExpect(model().attributeExists("email"))
                .andExpect(authenticated());

        Account newAccount = accountRepository.findByEmail("asdf@email.com");
        //TODO 메일 전송 테스트
        //then(javaMailSender).should(times(2)).send(any(SimpleMailMessage.class));
    }

    @DisplayName("[성공]이메일로 로그인 뷰")
    @Test
    public void emailLogin() throws Exception {
        mockMvc.perform(get("/email-login"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("emailLoginForm"))
                .andExpect(view().name("/email-login"));
    }

    @Test
    public void 로그인링크_이메일보내기() throws Exception {
        //given
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setPassword("123123123");
        signUpForm.setEmail("insookim0702@gmail.com");
        signUpForm.setNickname("devkis");
        accountService.processSignUp(signUpForm);

        //then
        mockMvc.perform(post("/email-login").param("email","insookim0702@gmail.com")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/email-login"))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"));
        //TODO 메일 전송 테스트
        //then(javaMailSender).should().send();
    }

    @Test
    public void 로그인링크_로그인() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("devkis");
        signUpForm.setEmail("insookim0702@gmail.com");
        signUpForm.setPassword("123123123");
        Account account = accountService.processSignUp(signUpForm);

        mockMvc.perform(get("/login-by-email").param("email","insookim0702@gmail.com")
        .param("token",account.getEmailCheckToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("/logged-in-email"));
    }
}