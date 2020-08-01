package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("[성공]프로필 설정 페이지 요청")
    @WithAccount("devkis")
    @Test
    void settingProfile() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_PROFILE))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PROFILE))
                .andExpect(authenticated());
    }

    @DisplayName("[성공]프로필 수정")
    @WithAccount("devkis")
    @Test
    void updateProfile() throws Exception {
        String bio = "한 줄 수정.";
        mockMvc.perform(post(SettingsController.SETTING_PROFILE)
                .param("bio",bio)
                .param("personalUrl","http://pinokio0702.tistory.com")
                .param("occupation","solutionEngineer")
                .param("livingArea","하남시")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("devkis");
        assertEquals(bio, account.getBio());
    }

    @DisplayName("[실패]프로필 수정")
    @WithAccount("devkis")
    @Test
    void FailUpdateProfile() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_PROFILE)
                .param("bio","import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                        "import com.studyolle.infra.ContainerBaseTest;\n" +
                        "import com.studyolle.infra.MockMvcTest;\n" +
                        "import com.studyolle.modules.tag.Tag;\n" +
                        "import com.studyolle.modules.zone.Zone;\n" +
                        "import com.studyolle.modules.tag.TagForm;\n" +
                        "import com.studyolle.modules.zone.ZoneForm;\n" +
                        "import com.studyolle.modules.tag.TagRepository;\n" +
                        "import com.studyolle.modules.zone.ZoneRepository;\n" +
                        "import org.junit.jupiter.api.AfterEach;\n" +
                        "import org.junit.jupiter.api.BeforeEach;\n" +
                        "import org.junit.jupiter.api.DisplayName;\n" +
                        "import org.junit.jupiter.api.Test;\n" +
                        "import org.springframework.beans.factory.annotation.Autowired;\n" +
                        "import org.springframework.http.MediaType;")
                .param("personalUrl","http://pinokio0702.tistory.com")
                .param("occupation","solutionEngineer")
                .param("livingArea","하남시")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PROFILE))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profileForm"));

        //Account account = accountRepository.findByNickname("devkis");
        //assertNull(account.getBio());
    }

    @DisplayName("패스워드 변경 뷰")
    @WithAccount("devkis")
    @Test
    void viewpageTest() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name(SettingsController.SETTING_PASSWORD));
    }

    @DisplayName("[성공]비밀번호 변경")
    @WithAccount("devkis")
    @Test
    void updatePasswordSuccess() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_PASSWORD)
                .param("newPassword","123123123")
                .param("checkNewPassword","123123123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(model().attributeDoesNotExist("errors"))
                .andExpect(redirectedUrl(SettingsController.SETTING_PASSWORD));
        assertTrue(passwordEncoder.matches("123123123", accountRepository.findByNickname("devkis").getPassword()));
    }

    @DisplayName("[실패]비밀번호 변경")
    @WithAccount("devkis")
    @Test
    void updatePasswordFail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_PASSWORD)
                .param("newPassword","123123123")
                .param("checkNewPassword","123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"));
    }

    @DisplayName("[성공]닉네임변경 뷰")
    @WithAccount("devkis")
    @Test
    void settingAccountTest() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_ACCOUNT))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("accountForm"))
                .andExpect(model().attributeDoesNotExist("errors"));
    }

    @DisplayName("[성공]닉네임 변경하기")
    @WithAccount("devkis")
    @Test
    void updateNickname() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_ACCOUNT)
                .param("newNickname","에어컨")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_ACCOUNT))
                .andExpect(flash().attributeExists("message"))
                .andExpect(model().hasNoErrors())
                .andExpect(authenticated().withUsername("에어컨"));
        assertTrue(accountRepository.existsByNickname("에어컨"));
    }

    @DisplayName("[실패]중복 닉네임 변경하기")
    @WithAccount("devkis")
    @Test
    void updateNicknameFail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_ACCOUNT)
                .param("newNickname","devkis")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(model().attributeDoesNotExist("message"))
            .andExpect(model().hasErrors())
            .andExpect(view().name(SettingsController.SETTING_ACCOUNT))
            .andExpect(authenticated().withUsername("devkis"));
    }
}