package com.studyolle.modules.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;


    @Test
    void 이메일인증토큰() throws Exception {
        mockMvc.perform(post("/sign-up")
        .param("nickname","ekekekkeke")
        .param("email","insookim0702@gmail.com")
        .param("password","123123123").with(csrf()))
        .andExpect(status().is3xxRedirection());
        Account account = accountRepository.findByEmail("insookim0702@gmail.com");
        assertNotNull(account.getEmailCheckToken());
    }
}