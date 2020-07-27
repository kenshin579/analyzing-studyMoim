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
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
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

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("[성]프로필 설정 페이지 요청")
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


}