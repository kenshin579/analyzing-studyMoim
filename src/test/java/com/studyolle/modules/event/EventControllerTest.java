package com.studyolle.modules.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.account.WithAccount;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.AccountService;
import com.studyolle.modules.account.form.SignUpForm;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyForm;
import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.StudyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    @Autowired private ObjectMapper objectMapper;
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

    @DisplayName("[성공 케이스 200]모임 생성 뷰 페이지")
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

    @DisplayName("[성공 케이스 302]모임 생성 서브밋")
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

    @DisplayName("[실패 케이스 200]모임 생성 서브밋 검증 실패")
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

    @DisplayName("[성공 케이스 200]모임 정보 뷰 페이지")
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

    @DisplayName("[성공 케이스 200]모임 수정 뷰 페이지")
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

    @DisplayName("[성공 케이스 302]모임 정보 변경 업데이트 서브밋")
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

    @DisplayName("[성공 케이스 302]모임 삭제 기능")
    @WithAccount("devkis")
    @Test
    void removeEvent() throws Exception {
        Account account = accountRepository.findByNickname("devkis");
        Study study = studyRepository.findByPath("spring");
        assertNotNull(study);
        Event event = createEvent("모임을 만들자", EventType.FCFS, 2, study, account);
        assertNotNull(eventRepository.findByTitle("모임을 만들자"));
        Account member1 = createAccount("member1");
        Account member2 = createAccount("member2");
        eventService.newEnrollment(event, member1);
        eventService.newEnrollment(event, member2);
        assertNotNull(enrollmentService.getEnrollment(event, member1));
        assertNotNull(enrollmentService.getEnrollment(event, member2));

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/remove")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events"));
        assertNull(eventRepository.findByTitle("모임을 만들자"));
        assertNull(enrollmentService.getEnrollment(event, member1));
        assertNull(enrollmentService.getEnrollment(event, member2));
    }

    @DisplayName("[성공 케이스 302]멤버의 모임 참가 신청 -자동 수락")
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



    @DisplayName("[성공 케이스 302]선착순 모임에 참가 신청 - 대기 중(이미 인원이 꽉차서)")
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

    @DisplayName("[성공 케이스 302]선착순 모임에 참가 신청 - 선착순 안에 든 멤버의 모임 취소")
    @WithAccount("devkis")
    @Test
    void  removeEnrollment_to_FCFS_event() throws Exception{
        Account account = createAccount("master");
        Study study = studyRepository.findByPath("spring");
        assertNotNull(study);
        Event event = createEvent("주중정기모임", EventType.FCFS, 2, study, account);
        assertNotNull(eventRepository.findByTitle("주중정기모임"));

        eventService.newEnrollment(event, accountRepository.findByNickname("devkis"));

        mockMvc.perform(post("/study/spring/events/"+event.getId()+"/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events/"+event.getId()));
        event = eventRepository.findById(Long.valueOf(event.getId())).orElseThrow();
        account = accountRepository.findByNickname("devkis");
        assertNull(enrollmentRepository.findByEventAndAccount(event, account));
    }

    @DisplayName("[성공 케이스 302]선착순 모임에 멤버가 참가 신청 - 선착순 인원 모집이 끝난 상태에서 선착순에 든 멤버가 모임을 취소해서 선착순 밖에 다른 인원이 모임에 자동으로 참여 승인이 되는 테스트")
    @WithAccount("devkis")
    @Test
    void removeEnrollment_to_FCFS_event_Accepted() throws Exception {
        Account master = createAccount("master");
        Study study = studyRepository.findByPath("spring");
        Event event = createEvent("정기 모임", EventType.FCFS, 2, study, master);
        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(event, may);
        Account devkis = accountRepository.findByNickname("devkis");
        eventService.newEnrollment(event, devkis);
        eventService.newEnrollment(event, june);
        Enrollment enrollment = enrollmentService.getEnrollment(event, june);
        assertFalse(enrollment.isAccepted());

        mockMvc.perform(post("/study/spring/events/"+event.getId()+"/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events/"+ event.getId()));

        enrollment = enrollmentService.getEnrollment(event, june);
        assertTrue(enrollment.isAccepted());
    }

    @DisplayName("[성공 케이스 302]선착순 모임에 참가 신청 - 선착순 모임을 관리자가 2명에서 3명으로 인원을 늘리면, 3번째로 모임을 신청한 멤버는 자동으로 '참가 승인'이 되어야 함.")
    @WithAccount("devkis")
    @Test
    void updateEvent_expand_limitEnroll() throws Exception {
        Account master = createAccount("master");
        Study study = studyRepository.findByPath("spring");
        Event event = createEvent("정기모임", EventType.FCFS, 2, study, master);
        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, master);
        eventService.newEnrollment(event, june);
        assertFalse(enrollmentService.getEnrollment(event, june).isAccepted());

        mockMvc.perform(post("/study/spring/events/"+event.getId()+"/edit")
                .with(csrf())
                .param("title","정기모임")
                .param("limitOfEnrollments",String.valueOf(11))
                .param("description", "스프링을 처음부터 공부하기 위한 모임입니다.")
                .param("endEnrollmentDateTime", String.valueOf(LocalDateTime.now().plusHours(1)))
                .param("startDateTime", String.valueOf(LocalDateTime.now().plusHours(3)))
                .param("endDateTime", String.valueOf(LocalDateTime.now().plusHours(7)))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/spring/events/"+event.getId()));
        event = eventService.getEvent(event.getId());
        assertTrue(event.getLimitOfEnrollments() ==11);
        assertTrue(enrollmentService.getEnrollment(event, june).isAccepted());
    }

    @DisplayName("[성공 케이스 200 ajax]이벤트 타입이 '관리자 확인'일 때, 관리자가 '승인'으로 모임 참여를 신청한 멤버의 참여 승인을 확정하기")
    @WithAccount("devkis")
    @Test
    void approveEnrollment() throws Exception {
        Account devkis = accountRepository.findByNickname("devkis");
        Study study = studyRepository.findByPath("spring");
        Account member1 = createAccount("member1");
        Event event = createEvent("모이자", EventType.CONFIRMATIVE, 3, study, devkis);
        eventService.newEnrollment(event, member1);
        Enrollment enrollment = enrollmentService.getEnrollment(event, member1);
        assertFalse(enrollment.isAccepted());

        EnrollForm enrollForm = new EnrollForm();
        enrollForm.setId(String.valueOf(enrollment.getId()));

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/approveEnrollment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollForm))
                .with(csrf())
        )
                .andExpect(status().isOk());
        assertTrue(enrollmentService.getEnrollment(event, member1).isAccepted());
    }

    @DisplayName("[성공 케이스 200 ajax]이벤트 타입이 '관리자 확인'일 때, 관리자가 '취소'로 모임 참여를 신청한 멤버의 참여 승인을 취소시키기")
    @WithAccount("devkis")
    @Test
    void rejectEnrollment() throws Exception {
        Account devkis = accountRepository.findByNickname("devkis");
        Study study = studyRepository.findByPath("spring");
        Account member1 = createAccount("member1");
        Event event = createEvent("모이자", EventType.CONFIRMATIVE, 3, study, devkis);
        eventService.newEnrollment(event, member1);
        Enrollment enrollment = enrollmentService.getEnrollment(event, member1);
        assertFalse(enrollment.isAccepted());
        eventService.acceptEnrollment(event, enrollment);
        assertTrue(enrollment.isAccepted());

        EnrollForm enrollForm = new EnrollForm();
        enrollForm.setId(String.valueOf(enrollment.getId()));

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/cancelEnrollment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollForm))
                .with(csrf())
        )
                .andExpect(status().isOk());
        assertFalse(enrollmentService.getEnrollment(event, member1).isAccepted());
    }

    @DisplayName("[성공 케이스 302]모임 주관자가 참석 승인 멤버 출석 체크하기")
    @WithAccount("devkis")
    @Test
    void checkinEnrollment() throws Exception {
        Study study = studyRepository.findByPath("spring");
        Account devkis = accountRepository.findByNickname("devkis");
        Account member1 = createAccount("member1");
        Event event = createEvent("모이자", EventType.CONFIRMATIVE, 3, study, devkis);
        eventService.newEnrollment(event, member1);
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event,member1);
        assertFalse(enrollment.isAttend());

        mockMvc.perform(get("/study/"+study.getPath()+"/events/"+event.getId()+"/enrollments/"+enrollment.getId()+"/checkin")
                            .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/events/"+event.getId()));

        assertTrue(enrollmentService.getEnrollment(event, member1).isAttend());
    }

    @DisplayName("[성공 케이스 302]모임 주관자가 모임 참성 승인된 멤버의 출석 체크 취소하기")
    @WithAccount("devkis")
    @Test
    void checkoutEnrollment() throws Exception {
        Study study = studyRepository.findByPath("spring");
        Account devkis = accountRepository.findByNickname("devkis");
        Account member1 = createAccount("member1");
        Event event = createEvent("모이자", EventType.CONFIRMATIVE, 3, study, devkis);
        eventService.newEnrollment(event, member1);
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event,member1);
        eventService.checkInEnrollment(enrollment);
        assertTrue(enrollment.isAttend());

        mockMvc.perform(get("/study/"+study.getPath()+"/events/"+event.getId()+"/enrollments/"+enrollment.getId()+"/checkout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/events/"+event.getId()));

        assertFalse(enrollmentRepository.findByEventAndAccount(event, member1).isAttend());
    }
}