package com.studyolle.study;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "classpath:application.properties")
class StudySettingsControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ModelMapper modelMapper;
    @Autowired private StudyRepository studyRepository;
    @Autowired private StudyService studyService;
    @Autowired private AccountRepository accountRepository;

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

    @DisplayName("[실패] 100자 넘어서 수정 실패")
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
}
