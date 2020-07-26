package com.studyolle.settings;

import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@RunWith(SpringRunner.class)
public class profileTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Before
    public void before() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("kis@email.com");
        signUpForm.setNickname("devkis");
        signUpForm.setPassword("123123123");
        Account account = accountService.processSignUp(signUpForm);
        accountService.login(account);
    }
    @After
    public void after(){
        accountRepository.deleteAll();
    }

    @Test
    public void 프로필설정_뷰페이지() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(authenticated());
    }

    @Test
    public void 프로필설정_트랜젝션() throws Exception {
        String bio = "한 줄 입력하기";
        mockMvc.perform(post("/settings/profile")
                            .param("bio",bio)
                            .param("personalUrl","http://pinokio0702.tistory.com")
                            .param("occupation","solutionEngineer")
                            .param("livingArea","하남시")
                            .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_PROFILE))
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated());

        Account devkis = accountRepository.findByNickname("devkis");
        assertEquals(bio, devkis.getBio());
    }
}

