package com.studyolle.event;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.*;
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

import static org.junit.jupiter.api.Assertions.*;
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
    @Autowired private AccountService accountService;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private EnrollmentService enrollmentService;
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
        enrollmentRepository.deleteAll();
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private Account createAccount(String nickname) {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setEmail(nickname+"@gmail.com");
        signUpForm.setPassword("123123123");
        Account account = accountService.processSignUp(signUpForm);
        return account;
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

    private Long createEvent1(String title, EventType eventType, int limit, Study study, Account account){
        EventForm eventForm = new EventForm();
        eventForm.setTitle(title);
        eventForm.setEventType(eventType);
        eventForm.setDescription("스프링을 처음부터 공부하기 위한 모임입니다.");
        eventForm.setLimitOfEnrollments(limit);
        eventForm.setEndEnrollmentDateTime(LocalDateTime.now().plusHours(1));
        eventForm.setStartDateTime(LocalDateTime.now().plusHours(3));
        eventForm.setEndDateTime(LocalDateTime.now().plusHours(7));
        Event event = modelMapper.map(eventForm, Event.class);
        Event newEvent = eventService.createEvent(study, event, account);
        assertNotNull(newEvent);
        Long eventId = eventRepository.findAll().get(0).getId();
        return eventId;
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(study, event, account);
    }

    @DisplayName("모임 뷰페이지")
    @WithAccount("devkis")
    @Test
    void 모임뷰() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyRepository.findByPath("spring");
        assertNotNull(study);
        Event event = createEvent("모임을 만들자", EventType.FCFS, 2, study, account);
        mockMvc.perform(get("/study/spring/events/"+event.getId()))
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
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyRepository.findByPath("spring");
        assertNotNull(study);
        Event event = createEvent("모임을 만들자", EventType.FCFS, 2, study, account);
        mockMvc.perform(get("/study/spring/events/"+event.getId()+"/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(view().name("event/edit"));
    }

    @DisplayName("모임 정보 변경 업데이트 서브밋")
    @WithAccount("devkis")
    @Test
    void 모임업데이트서브밋() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyRepository.findByPath("spring");
        assertNotNull(study);
        Event event = createEvent("모임을 만들자", EventType.FCFS, 2, study, account);
        mockMvc.perform(post("/study/spring/events/"+event.getId()+"/edit")
                .param("title","모임을 만들자2")
                .param("limitOfEnrollments",String.valueOf(11))
                .param("description", "스프링을 처음부터 공부하기 위한 모임입니다.")
                .param("endEnrollmentDateTime", String.valueOf(LocalDateTime.now().plusHours(1)))
                .param("startDateTime", String.valueOf(LocalDateTime.now().plusHours(3)))
                .param("endDateTime", String.valueOf(LocalDateTime.now().plusHours(7)))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/study/spring/events/"+event.getId()));

        assertNotNull(eventRepository.findByTitle("모임을 만들자2"));
    }

    @DisplayName("모임 삭제 기능")
    @WithAccount("devkis")
    @Test
    void removeEvent() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyRepository.findByPath("spring");
        assertNotNull(study);
        Event event = createEvent("모임을 만들자", EventType.FCFS, 2, study, account);
        assertNotNull(eventRepository.findByTitle("모임을 만들자"));
        mockMvc.perform(post("/study/spring/events/"+event.getId()+"/remove")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events"));
        assertNull(eventRepository.findByTitle("모임을 만들자"));
    }

    @DisplayName("멤버의 모임 참가 신청 -자동 수락")
    @WithAccount("devkis")
    @Test
    void enrollEvent() throws Exception {
        Account account = createAccount("master");
        Study study = studyRepository.findByPath("spring");
        Event event = createEvent("모임을 만들자", EventType.FCFS, 2, study, account);
        assertNotNull(study);
        assertNotNull(eventRepository.findByTitle("모임을 만들자"));
        mockMvc.perform(post("/study/spring/events/"+event.getId()+"/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events/"+event.getId()));
        event = eventRepository.findById(Long.valueOf(event.getId())).orElseThrow();

        account = accountRepository.findByNickname("devkis");
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }



    @DisplayName("선착순 모임에 참가 신청 - 대기 중(이미 인원이 꽉차서)")
    @WithAccount("devkis")
    @Test
    void  newEnrollment_to_FCFS_event_not_accepted() throws Exception{
        Account account = createAccount("master");
        Study study = studyRepository.findByPath("spring");
        Event event = createEvent("주중정기모임", EventType.FCFS, 2, study, account);
        //Long id = createEvent1("주중정기모임", EventType.FCFS, 2, study, account);
        //Event event = eventRepository.findById(id).orElseThrow();
        assertNotNull(study);
        assertNotNull(eventRepository.findByTitle("주중정기모임"));
        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/study/spring/events/"+event.getId()+"/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events/"+event.getId()));
        event = eventRepository.findById(Long.valueOf(event.getId())).orElseThrow();
        account = accountRepository.findByNickname("devkis");
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        assertFalse(enrollment.isAccepted());
    }
}