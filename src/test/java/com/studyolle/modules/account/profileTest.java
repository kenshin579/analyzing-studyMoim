package com.studyolle.modules.account;

import org.junit.After;
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
    private AccountRepository accountRepository;

    @After
    public void after(){
        accountRepository.deleteAll();
    }

    @WithAccount("devkis")
    @Test
    public void 프로필설정_뷰페이지() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_PROFILE))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PROFILE))
                .andExpect(authenticated());
    }

    @WithAccount("devkis")
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

