package com.studyolle.event;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.study.StudyForm;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.StudyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
class EventControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ModelMapper modelMapper;
    @Autowired private StudyService studyService;
    @Autowired private EventService eventService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private StudyRepository studyRepository;
    @Autowired private EventRepository eventRepository;
    @WithAccount("devkis")

    @BeforeEach
    void beforeEach() {
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("spring");
        studyForm.setTitle("스프링스터디모임");
        studyForm.setShortDescription("스프링 스터디 모임입니다.");
        studyForm.setFullDescription("스프링 스터디 모입입니다. 함께해보아요~");
        Account devkis = accountRepository.findByNickname("devkis");
        studyService.saveStudy(modelMapper.map(studyForm, Study.class), devkis);
    }

    @AfterEach
    void after(){
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("모임 생성 뷰페이지")
    @WithAccount("devkis")
    @Test
    void 모임폼뷰() throws Exception {
        assertNotNull(studyRepository.findByPath("spring"));
        mockMvc.perform(get("/study/spring/new-event"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(view().name("event/form"));
    }

    @DisplayName("모임생성 서브밋")
    @WithAccount("devkis")
    @Test
    void 모임서브밋() throws Exception {
        assertNotNull(studyRepository.findByPath("spring"));
        mockMvc.perform(post("/study/spring/new-event")
                .param("title","새 모임 만들기")
                .param("description", "스프링을 처음부터 공부하기 위한 모임입니다.")
                .param("endEnrollmentDateTime", String.valueOf(LocalDateTime.now().plusHours(1)))
                .param("startDateTime", String.valueOf(LocalDateTime.now().plusHours(3)))
                .param("endDateTime", String.valueOf(LocalDateTime.now().plusHours(7)))
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @DisplayName("모임생성 서브밋 검증 실패")
    @WithAccount("devkis")
    @Test
    void 모임서브밋검증실패() throws Exception {
        assertNotNull(studyRepository.findByPath("spring"));
        mockMvc.perform(post("/study/spring/new-event")
                .param("title","새 모임 만들기")
                .param("description", "스프링을 처음부터 공부하기 위한 모임입니다.")
                .param("endEnrollmentDateTime", String.valueOf(LocalDateTime.now().minusHours(3)))
                .param("startDateTime", String.valueOf(LocalDateTime.now().plusHours(3)))
                .param("endDateTime", String.valueOf(LocalDateTime.now().plusHours(7)))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"));
    }

    private String createEvent(){
        Study study = studyRepository.findByPath("spring");
        EventForm eventForm = new EventForm();
        eventForm.setTitle("모임을 만들자");
        eventForm.setDescription("스프링을 처음부터 공부하기 위한 모임입니다.");
        eventForm.setEndEnrollmentDateTime(LocalDateTime.now().plusHours(1));
        eventForm.setStartDateTime(LocalDateTime.now().plusHours(3));
        eventForm.setEndDateTime(LocalDateTime.now().plusHours(7));
        Event event = modelMapper.map(eventForm, Event.class);
        Event newEvent = eventService.createEvent(study, event, accountRepository.findByNickname("devkis"));
        assertNotNull(newEvent);
        String eventId = eventRepository.findAll().get(0).getId() +"";
        return eventId;
    }

    @DisplayName("모임 뷰페이지")
    @WithAccount("devkis")
    @Test
    void 모임뷰() throws Exception {
        String id = createEvent();
        mockMvc.perform(get("/study/spring/events/"+id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("event"))
                .andExpect(view().name("event/view"));
    }

    @DisplayName("모임 수정 페이지 뷰")
    @WithAccount("devkis")
    @Test
    void 모임수정뷰() throws Exception {
        String id = createEvent();
        mockMvc.perform(get("/study/spring/events/"+id+"/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(view().name("event/edit"));
    }

    @DisplayName("모임 업데이트 서브밋")
    @WithAccount("devkis")
    @Test
    void 모임업데이트서브밋() throws Exception {
        assertNotNull(studyRepository.findByPath("spring"));
        String id = createEvent();
        Event event = eventRepository.findByTitle("모임을 만들자");
        assertNotNull(event);
        mockMvc.perform(post("/study/spring/events/"+id+"/edit")
                .param("title","모임을 만들자2")
                .param("limitOfEnrollments",String.valueOf(11))
                .param("description", "스프링을 처음부터 공부하기 위한 모임입니다.")
                .param("endEnrollmentDateTime", String.valueOf(LocalDateTime.now().plusHours(1)))
                .param("startDateTime", String.valueOf(LocalDateTime.now().plusHours(3)))
                .param("endDateTime", String.valueOf(LocalDateTime.now().plusHours(7)))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/spring/events/"+id));

        assertNotNull(eventRepository.findByTitle("모임을 만들자2"));
    }

    @DisplayName("모임 취소 기능")
    @WithAccount("devkis")
    @Test
    void removeEvent() throws Exception {
        assertNotNull(studyRepository.findByPath("spring"));
        String id = createEvent();
        assertNotNull(eventRepository.findByTitle("모임을 만들자"));
        mockMvc.perform(post("/study/spring/events/"+id+"/remove")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events"));
        assertNull(eventRepository.findByTitle("모임을 만들자"));
    }
}