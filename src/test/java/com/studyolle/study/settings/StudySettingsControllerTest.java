package com.studyolle.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.settings.form.TagForm;
import com.studyolle.settings.form.ZoneForm;
import com.studyolle.tag.TagRepository;
import com.studyolle.tag.TagService;
import com.studyolle.zone.ZoneRepository;
import com.studyolle.zone.ZoneService;
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

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "classpath:application.properties")
class StudySettingsControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private StudyRepository studyRepository;
    @Autowired private StudyService studyService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private ZoneRepository zoneRepository;
    @Autowired private TagService tagService;
    @Autowired private ZoneService zoneService;

    @WithAccount("devkis")
    @BeforeEach
    void beforeEach(){
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study-spring");
        studyForm.setTitle("스프링스터디모임");
        studyForm.setShortDescription("스프링 스터디 모임입니다.");
        studyForm.setFullDescription("스프링 스터디 모입입니다. 함께해보아요~");
        Account devkis = accountRepository.findByNickname("devkis");
        studyService.saveStudy(modelMapper.map(studyForm, Study.class), devkis);
    }

    @AfterEach
    private void afterEach(){
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
                .param("shortDescription","짧은수정완료")
                .param("fullDescription","상세설명수정완료")
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
                .param("shortDescription","스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.")
                .param("fullDescription","스프링 스터디 모입입니다. 함께해보아요~")
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
}
