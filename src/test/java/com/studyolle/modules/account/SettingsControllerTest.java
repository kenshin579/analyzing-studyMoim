package com.studyolle.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.account.form.TagForm;
import com.studyolle.modules.account.form.ZoneForm;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.zone.ZoneRepository;
import com.studyolle.modules.zone.ZoneService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(properties = "classpath:application.properties")
class SettingsControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired TagRepository tagRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired AccountService accountService;
    @Autowired ZoneService zoneService;
    @Autowired ZoneRepository zoneRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("[성공 케이스]프로필 설정 뷰 페이지 요청")
    @WithAccount("devkis")
    @Test
    void settingProfile() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_PROFILE))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PROFILE))
                .andExpect(authenticated());
    }

    @DisplayName("[성공 케이스]프로필 수정")
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

    @DisplayName("[실패 케이스]프로필 수정")
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

        Account account = accountRepository.findByNickname("devkis");
        assertNull(account.getBio());
    }

    @DisplayName("[성공 케이스 200]패스워드 변경 뷰 페이지")
    @WithAccount("devkis")
    @Test
    void viewpageTest() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name(SettingsController.SETTING_PASSWORD));
    }

    @DisplayName("[성공 케이스]비밀번호 변경")
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

    @DisplayName("[실패 케이스]비밀번호 변경")
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

    @DisplayName("[성공 케이스 200]알림 뷰 페이지")
    @WithAccount("devkis")
    @Test
    public void settingsNotification() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_NOTIFICATIONS))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_NOTIFICATIONS))
                .andExpect(model().attributeExists("account"));
    }
    @DisplayName("[성공 케이스]알림 변경 설정 테스트")
    @WithAccount("devkis")
    @Test
    public void updateNotification() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_NOTIFICATIONS)
                .param("alarmStudyCreationToEmail", String.valueOf(false))
                .param("alarmStudyCreationToWeb", String.valueOf(false))
                .param("alarmApplyResultToEmail", String.valueOf(false))
                .param("alarmApplyResultToWeb", String.valueOf(false))
                .param("alarmUpdateInfoToEmail", String.valueOf(false))
                .param("alarmUpdateInfoToWeb", String.valueOf(false))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_NOTIFICATIONS))
                .andExpect(flash().attributeExists("message"));
        assertFalse(accountRepository.findByNickname("devkis").isAlarmApplyResultToEmail());
        assertFalse(accountRepository.findByNickname("devkis").isAlarmApplyResultToWeb());
        assertFalse(accountRepository.findByNickname("devkis").isAlarmStudyCreationToEmail());
        assertFalse(accountRepository.findByNickname("devkis").isAlarmStudyCreationToWeb());
        assertFalse(accountRepository.findByNickname("devkis").isAlarmUpdateInfoToEmail());
        assertFalse(accountRepository.findByNickname("devkis").isAlarmUpdateInfoToWeb());
    }
    @DisplayName("[성공 케이스 200]닉네임변경 뷰 페이지")
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

    @DisplayName("[성공 케이스]닉네임 변경하기")
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

    @DisplayName("[실패 케이스]중복 닉네임 변경하기")
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

    @DisplayName("[성공 케이스 200]관심주제 등록 뷰 페이지")
    @WithAccount("devkis")
    @Test
    void TagPage() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_TAG))
                .andExpect(status().isOk());
    }

    @DisplayName("[성공 케이스]태그 등록하기")
    @WithAccount("devkis")
    @Test
    void settingTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Springboot");

        mockMvc.perform(post(SettingsController.SETTING_TAG+"/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
        .andExpect(status().isOk());
        Tag tag = tagRepository.findByTitle("Springboot");
        assertNotNull(tag);
        Account devkis = accountRepository.findByNickname("devkis");
        assertTrue(devkis.getTags().contains(tag));
    }

    @DisplayName("[성공 케이스]태그 삭제하기")
    @WithAccount("devkis")
    @Test
    void removeTag() throws Exception {
        //given
        Tag tag = new Tag();
        tag.setTitle("Springboot");
        tagRepository.save(tag);
        Account devkis = accountRepository.findByNickname("devkis");
        accountService.addTag(devkis, tag);
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Springboot");

        assertNotNull(tagRepository.findByTitle("Springboot"));
        assertTrue(devkis.getTags().contains(tag));

        //Then
        mockMvc.perform(post(SettingsController.SETTING_TAG+"/remove")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tagForm))
                    .with(csrf()))
                .andExpect(status().isOk());

        Tag springboot = tagRepository.findByTitle("Springboot");
        assertNotNull(springboot);
        assertTrue(devkis.getTags().isEmpty());
    }

    @DisplayName("[성공 케이스 200]위치 뷰 페이지")
    @WithAccount("devkis")
    @Test
    void settingZone() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_ZONE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(view().name(SettingsController.SETTING_ZONE));
    }

    @DisplayName("[성공 케이스]위치추가")
    @WithAccount("devkis")
    @Test
    void addZone() throws Exception {
        zoneService.initZoneData();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("안양시(Anyang)/Gyeonggi");
        Zone byCityAndProvince = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        assertNotNull(byCityAndProvince);

        //then
        mockMvc.perform(post(SettingsController.SETTING_ZONE+"/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(zoneForm))
        .with(csrf()))
                .andExpect(status().isOk());
        Account devkis = accountRepository.findByNickname("devkis");
        assertTrue(devkis.getZone().contains(byCityAndProvince));
    }


    @DisplayName("[실패 케이스]위치 추가 실패 케이스 - whitelist에 없는 걸로 등록")
    @WithAccount("devkis")
    @Test
    void failAddZone() throws Exception {
        //TODO whitelist에 없는 값이 들어왔을 경우로 처리해야함. Validate를 추가한 거로 테스트 변경해야함.
        //given
        ZoneForm zoneForm = new ZoneForm();
        zoneService.initZoneData();
        zoneForm.setZoneName("안양시1(few)/neonow");
        Zone byCityAndProvince = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        assertNull(byCityAndProvince);

        mockMvc.perform(post(SettingsController.SETTING_ZONE+"/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        Account devkis = accountRepository.findByNickname("devkis");
        assertFalse(devkis.getZone().contains(byCityAndProvince));
    }

    @DisplayName("[성공 케이스]위치 삭제")
    @WithAccount("devkis")
    @Test
    void removeZone() throws Exception {
        //Given
        ZoneForm zoneForm = new ZoneForm();
        zoneService.initZoneData();
        zoneForm.setZoneName("안양시(Anyang)/Gyeonggi");
        Zone byCityAndProvince = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        Account devkis = accountRepository.findByNickname("devkis");
        accountService.addZone(devkis, byCityAndProvince);
        assertNotNull(byCityAndProvince);
        //Then
        mockMvc.perform(post(SettingsController.SETTING_ZONE+"/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
        .with(csrf()))
                .andExpect(status().isOk());


        assertFalse(devkis.getZone().contains(byCityAndProvince));
    }


}