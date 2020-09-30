package com.studyolle.modules.study;

import com.studyolle.modules.account.WithAccount;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.Account;
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

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "classpath:application.properties")
class StudyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private StudyRepository studyRepository;
    @Autowired private StudyService studyService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private ModelMapper modelMapper;

    @AfterEach void afterEach(){
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("[성공 케이스 200]스터디 생성 뷰 페이지")
    @WithAccount("devkis")
    @Test
    public void studyFormView() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("/study/new-study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @DisplayName("[성공 케이스] 스터디 생성 성공 테스트")
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

    @DisplayName("[실패 케이스]짧은 소개가 100자가 넘어서 실패")
    @WithAccount("devkis")
    @Test
    void 실패_스터디생성() throws Exception {
        mockMvc.perform(post("/new-study").with(csrf())
                .param("title","스프링스터디모임")
                .param("path","study-spring")
                .param("shortDescription","스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.스프링 스터디 모임입니다.")
                .param("fullDescription","스프링 스터디 모입입니다. 함께해보아요~"))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk());
    }
    @WithAccount("devkis")
    private void createStudy(){
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("study-spring");
        studyForm.setTitle("스프링스터디모임");
        studyForm.setShortDescription("스프링 스터디 모임입니다.");
        studyForm.setFullDescription("스프링 스터디 모입입니다. 함께해보아요~");
        Account devkis = accountRepository.findByNickname("devkis");
        studyService.saveStudy(modelMapper.map(studyForm, Study.class), devkis);
    }

    @DisplayName("[성공 케이스]스터디 참여하기")
    @WithAccount("studyMember1")
    @Test
    void 스터디참여요청() throws Exception {
        createStudy();
        Account account = accountRepository.findByNickname("studyMember1");
        Study study = studyRepository.findByPath("study-spring");
        assertNotNull(study);
        assertNotNull(account);
        assertFalse(study.getMembers().contains(account));
        mockMvc.perform(post("/study/study-spring/join")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study-spring/members"));
        assertTrue(account.getNickname().equals("studyMember1"));
        Study updateStudy = studyRepository.findByPath("study-spring");
        assertTrue(updateStudy.getMembers().contains(account));
    }

    @DisplayName("[성공 케이스]스터디 탈퇴하기")
    @WithAccount("studyMember1")
    @Test
    void 스터디탈퇴요청() throws Exception {
        createStudy();
        Account account = accountRepository.findByNickname("studyMember1");
        Study study = studyRepository.findByPath("study-spring");
        studyService.addMember(study, account);
        assertNotNull(study);
        assertNotNull(account);
        assertTrue(study.getMembers().contains(account));
        mockMvc.perform(post("/study/study-spring/leave")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/study-spring/members"));
        assertTrue(account.getNickname().equals("studyMember1"));
        Study updateStudy = studyRepository.findByPath("study-spring");
        assertFalse(updateStudy.getMembers().contains(account));
    }
}