package com.studyolle.modules.account;

import com.studyolle.modules.account.form.SignUpForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class loginTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void 로그인페이지접속_테스트() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(unauthenticated());
    }

    @Before
    public void before(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("kis@email.com");
        signUpForm.setNickname("devkis");
        signUpForm.setPassword("123123123");
        accountService.processSignUp(signUpForm);
    }

    @After
    public void after(){
        accountRepository.deleteAll();
    }

    @Test
    public void 이메일로_로그인_성공_테스트() throws Exception {
        mockMvc.perform(post("/login")
                .param("username","kis@email.com")
                .param("password","123123123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("devkis"));
    }

    @Test
    public void 닉네임으로_로그인_성공_테스트() throws Exception {
        mockMvc.perform(post("/login")
                .param("username","devkis")
                .param("password","123123123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("devkis"));
    }

    @Test
    public void 로그인_실패_테스트() throws Exception {
        mockMvc.perform((post("/login"))
                .param("username", "eee@email.com")
                .param("password","123123123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    public void 로그아웃_테스트() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}
