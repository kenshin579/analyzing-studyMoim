package com.studyolle.study;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import org.junit.jupiter.api.AfterEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private StudyRepository studyRepository;
    @Autowired private StudyService studyService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private ModelMapper modelMapper;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("[성공]스터디 생성 폼 뷰")
    @WithAccount("devkis")
    @Test
    public void studyFormView() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("/study/new-study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @DisplayName("[성공]스터디 생성")
    @WithAccount("devkis")
    @Test
    public void createStudy() throws Exception {
        Account devkis = accountRepository.findByNickname("devkis");
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study-spring");
        studyForm.setTitle("스프링스터디모임");
        studyForm.setShortDescription("스프링 스터디 모임입니다.");
        studyForm.setFullDescription("스프링 스터디 모입입니다. 함께해보아요~");
        Study study = studyService.saveStudy(modelMapper.map(studyForm, Study.class), devkis);
        Study byPath = studyRepository.findByPath(study.getPath());
        assertTrue(byPath.getManagers().contains(devkis));
        assertNotNull(studyRepository.findByPath("study-spring"));
    }

    @DisplayName("[성공] 스터디 생성 POST")
    @WithAccount("devkis")
    @Test
    void 스터디_생성() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("title","스프링스터디모임")
                .param("path","study-spring")
                .param("shortDescription","스프링 스터디 모임입니다.")
                .param("fullDescription","스프링 스터디 모입입니다. 함께해보아요~")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrl("/study/study-spring"));

        Study byPath = studyRepository.findByPath("study-spring");
        assertNotNull(byPath);
        Account devkis = accountRepository.findByNickname("devkis");
        assertTrue(byPath.getManagers().contains(devkis));
    }

    @DisplayName("[실패]짧은 소개가 100자가 넘어서 실패")
    @WithAccount("devkis")
    @Test
    void 실패_서터디생성() throws Exception {
        mockMvc.perform(post("/new-study").with(csrf())
                .param("title","스프링스터디모임")
                .param("path","study-spring")
                .param("shortDescription","스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.")
                .param("fullDescription","스프링 스터디 모입입니다. 함께해보아요~"))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk());
    }
}