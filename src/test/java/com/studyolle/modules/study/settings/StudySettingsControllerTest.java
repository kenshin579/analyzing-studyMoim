package com.studyolle.modules.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.account.WithAccount;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.account.form.TagForm;
import com.studyolle.modules.account.form.ZoneForm;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.tag.TagService;
import com.studyolle.modules.zone.ZoneRepository;
import com.studyolle.modules.zone.ZoneService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "classpath:application.properties")
class StudySettingsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private StudyService studyService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private ZoneService zoneService;

    @WithAccount("devkis")
    @BeforeEach
    void beforeEach() {
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study-spring");
        studyForm.setTitle("스프링스터디모임");
        studyForm.setShortDescription("스프링 스터디 모임입니다.");
        studyForm.setFullDescription("스프링 스터디 모입입니다. 함께해보아요~");
        Account devkis = accountRepository.findByNickname("devkis");
        studyService.saveStudy(modelMapper.map(studyForm, Study.class), devkis);
    }

    @AfterEach
    private void afterEach() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @DisplayName("[성공] 스터디 내용 수정")
    @WithAccount("devkis")
    @Test
    void settingsStudyDescription() throws Exception {
        //given - 저장된 스터디 확인, 매니저인지 확인
        Study byPath = studyRepository.findByPath("study-spring");
        assertNotNull(byPath);
        Account devkis = accountRepository.findByNickname("devkis");
        assertTrue(byPath.getManagers().contains(devkis));
        //then
        mockMvc.perform(post("/study/study-spring/settings/description")
                .param("shortDescription", "짧은수정완료")
                .param("fullDescription", "상세설명수정완료")
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study-spring"))
                .andExpect(flash().attributeExists("message"));
        Study updatedStudy = studyRepository.findByPath("study-spring");

        assertTrue(updatedStudy.getShortDescription().equals("짧은수정완료"));
        assertTrue(updatedStudy.getFullDescription().equals("상세설명수정완료"));
    }

    @DisplayName("[실패] 내용 설명 100자 넘어서 수정 실패")
    @WithAccount("devkis")
    @Test
    void settingsFailDescription() throws Exception {
        //given - 저장된 스터디 확인, 매니저인지 확인
        Study byPath = studyRepository.findByPath("study-spring");
        assertNotNull(byPath);
        Account devkis = accountRepository.findByNickname("devkis");
        assertTrue(byPath.getManagers().contains(devkis));

        mockMvc.perform(post("/study/study-spring/settings/description")
                .param("shortDescription", "스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.")
                .param("fullDescription", "스프링 스터디 모입입니다. 함께해보아요~")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("/study/settings/description"));
    }

    @DisplayName("[성공] 뷰 - 스터디 태그 추가")
    @WithAccount("devkis")
    @Test
    public void viewTag() throws Exception {
        mockMvc.perform(get("/study/study-spring/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("study"));
    }

    @DisplayName("[성공] 스터디 태크 추가")
    @WithAccount("devkis")
    @Test
    public void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring Boot");
        mockMvc.perform(post("/study/study-spring/settings/tags/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm)))
                .andExpect(status().isOk());

        Tag spring_boot = tagRepository.findByTitle("Spring Boot");
        assertNotNull(spring_boot);
        assertTrue(studyRepository.findByPath("study-spring").getTags().contains(spring_boot));
    }

    @DisplayName("[성공] 스터디 태크 제거")
    @WithAccount("devkis")
    @Test
    public void removeTag() throws Exception {
        Study study = studyRepository.findByPath("study-spring");
        assertNotNull(study);
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("Spring Boot");
        Tag tag = tagService.saveTag(tagForm.getTagTitle());
        studyService.addTag(study, tag);
        assertTrue(study.getTags().contains(tag));

        mockMvc.perform(post("/study/study-spring/settings/tags/remove")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm)))
                .andExpect(status().isOk());

        study = studyRepository.findByPath("study-spring");
        assertFalse(study.getTags().contains(tag));
    }

    @DisplayName("[성공] 뷰-스터디 활동 지역 등록")
    @WithAccount("devkis")
    @Test
    public void viewZone() throws Exception {
        mockMvc.perform(get("/study/study-spring/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("/study/settings/zone"));
    }

    @DisplayName("[성공] 스터디 활동 지역 삭제")
    @WithAccount("devkis")
    @Test
    public void addZone() throws Exception {
        Study study = studyRepository.findByPath("study-spring");
        assertNotNull(study);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("서울특별시(Seoul)/none");
        Zone zone = new Zone();
        zone.setCity(zoneForm.getCityName());
        zone.setLocalNameOfCity(zoneForm.getLocalName());
        zone.setProvince(zoneForm.getProvinceName());

        studyService.addZone(study, zone);
        assertTrue(study.getZones().contains(zone));

        mockMvc.perform(post("/study/study-spring/settings/zones/remove")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm)))
                .andExpect(status().isOk());

        study = studyRepository.findByPath("study-spring");
        assertFalse(study.getZones().contains(zone));
    }

    @DisplayName("[200] 스터디 설정 변경 뷰")
    @WithAccount("devkis")
    @Test
    void settingsStudy() throws Exception {
        mockMvc.perform(get("/study/study-spring/settings/study"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("/study/settings/study"));
    }

    @DisplayName("[성공] 스터디 비공개 -> 공개 변경")
    @WithAccount("devkis")
    @Test
    void publishStudy() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyService.getStudyToUpdate(account, "study-spring");
        assertFalse(study.isPublished());
        mockMvc.perform(post("/study/study-spring/settings/study/publish")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        study = studyService.getStudyToUpdate(account, "study-spring");
        assertTrue(study.isPublished());
    }

    @DisplayName("[성공] 스터디 공개 -> 비공개 변경")
    @WithAccount("devkis")
    @Test
    void nonPublishStudy() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyService.getStudyToUpdate(account, "study-spring");
        study.setPublished(true);
        studyRepository.save(study);
        mockMvc.perform(post("/study/study-spring/settings/study/nonPublish")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
        study = studyService.getStudyToUpdate(account, "study-spring"); 
        assertFalse(study.isPublished());
    }

    @DisplayName("[성공] 스터디 인원 모집하기")
    @WithAccount("devkis")
    @Test
    void recruiting() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyService.getStudyToUpdate(account, "study-spring");
        studyService.publishStudy(study);
        studyRepository.save(study);
        assertFalse(study.isRecruiting());
        mockMvc.perform(post("/study/study-spring/settings/study/recruiting")
        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        study = studyService.getStudyToUpdate(account, "study-spring");
        assertTrue(study.getRecruitingUpdateDateTime().getHour() == LocalDateTime.now().getHour());
        assertTrue(study.isRecruiting());
    }

    @DisplayName("[성공] 스터디 인원 모집 종료하기")
    @WithAccount("devkis")
    @Test
    void stopRecruiting() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyService.getStudyToUpdate(account, "study-spring");
        studyService.publishStudy(study);
        studyService.recruiting(study);
        study.setRecruitingUpdateDateTime(study.getRecruitingUpdateDateTime().minusHours(2));
        studyRepository.save(study);
        assertTrue(study.isPublished());
        assertTrue(study.isRecruiting());
        assertTrue(study.isPossibleUpdateRecruiting());
        assertNotNull(study.getRecruitingUpdateDateTime());
        mockMvc.perform(post("/study/study-spring/settings/study/stopRecruiting")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
        study = studyService.getStudyToUpdate(account, "study-spring");
        assertTrue(study.getRecruitingUpdateDateTime().getHour() == LocalDateTime.now().getHour());
        assertFalse(study.isRecruiting());
    }

    @DisplayName("[성공] 스터디 이름 변경")
    @WithAccount("devkis")
    @Test
    void updateStudyName() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyService.getStudyToUpdate(account, "study-spring");
        assertTrue(study.getTitle().equals("스프링스터디모임"));
        mockMvc.perform(post("/study/study-spring/settings/study/updateStudyName")
                .param("newTitle", "스프링스프링스프링").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study-spring"));
        study = studyService.getStudyToUpdate(account, "study-spring");
        assertNotNull(study);
        assertTrue(study.getTitle().equals("스프링스프링스프링"));
    }

    @DisplayName("[성공]스터디 경로 변경")
    @WithAccount("devkis")
    @Test
    void updateStudyPath() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyService.getStudyToUpdate(account, "study-spring");
        assertNotNull(study);
        assertTrue(study.getPath().equals("study-spring"));
        mockMvc.perform(post("/study/study-spring/settings/study/updateStudyPath")
                .param("newPath", "spring")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring"));
        study = studyService.getStudyToUpdate(account, "spring");
        assertNotNull(study);
        assertNotNull(study.getPath().equals("spring"));
    }

    @DisplayName("[실패 케이스 테스트]스터디 경로 변경")
    @WithAccount("devkis")
    @Test
    void updateStudyPathFail() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyService.getStudyToUpdate(account, "study-spring");
        assertNotNull(study);
        assertTrue(study.getPath().equals("study-spring"));
        mockMvc.perform(post("/study/study-spring/settings/study/updateStudyPath")
                .param("newPath", "")
                .with(csrf()))
                .andExpect(status().isOk());
        study = studyService.getStudyToUpdate(account, "study-spring");
        assertNotNull(study);
    }

    @DisplayName("[성공] 스터디 비공개 -> 공개 변경 설정")
    @WithAccount("devkis")
    @Test
    void updateStudyPublish() throws Exception {
        mockMvc.perform(post("/study/study-spring/settings/study/publish")
                    .param("published", "true")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/study-spring/settings/study"));
    }

    @DisplayName("[성공] 스터디 삭제")
    @WithAccount("devkis")
    @Test
    void removeStudy() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        mockMvc.perform(post("/study/study-spring/settings/study/remove")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection());
        //Study study = studyService.getStudyToUpdate(account, "study-spring");
        //assertNotNull(study);
    }

    @DisplayName("스터디 > 모임 탭 뷰")
    @WithAccount("devkis")
    @Test
    void eventView() throws Exception {
        mockMvc.perform(get("/study/study-spring/events"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("newEvents"))
                .andExpect(model().attributeExists("oldEvents"))
                .andExpect(view().name("study/events"));
    }
}

