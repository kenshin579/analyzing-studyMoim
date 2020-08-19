package com.studyolle.study;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
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

        mockMvc.perform(post("/new-study")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/study-spring"));
    }
}