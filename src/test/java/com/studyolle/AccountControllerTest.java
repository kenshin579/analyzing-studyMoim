package com.studyolle;

import com.studyolle.account.AccountController;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 뷰페이지 테스트")
    @Test
    public void AccountTest() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
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
                .andExpect(view().name("account/sign-up"));
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
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}