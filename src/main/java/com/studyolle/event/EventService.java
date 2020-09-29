package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;

    public void removeEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event createEvent(Study study, Event event, Account account) {
        event.setCreatedDateTime(LocalDateTime.now());
        event.setCreateBy(account);
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public Map<String, List<Event>> getEvents(Study study) {
        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        Map<String, List<Event>> map = new HashMap<>();
        map.put("newEvents", events.stream().filter(a -> a.getEndDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        map.put("oldEvents", events.stream().filter(a -> a.getEndDateTime().isBefore(LocalDateTime.now())).collect(Collectors.toList()));
        return map;
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow();
    }

    public void updateEvent(Event event, @Valid EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.updateFCFSMemberAccept();
    }

    public void newEnrollment(Event event, Account account){
        if(!enrollmentRepository.existsByEventAndAccount(event,account)){
            Enrollment enrollment = new Enrollment();
            enrollment.setEvent(event);
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptEnroll());
            enrollment.setAccount(account);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    public void removeEnrollment(Event event, Account account){
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        event.removeEnrollment(enrollment); //관계 끊어주고
        enrollmentRepository.delete(enrollment); //삭제
        event.updateFCFSMemberAccept();
    }
    public void removeEnrollments(Event event) {
        List<Enrollment> enrollmentList = enrollmentRepository.findByEvent(event);
        for(Enrollment e : enrollmentList){
            event.removeEnrollment(e); //관계 끊어주고
            enrollmentRepository.delete(e); //삭제
        }

    }
    public void acceptEnrollment(Event event, Enrollment enrollment){
        event.accept(enrollment);
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttend(true);
    }

    public void checkOutEnrollment(Enrollment enrollment) {
        enrollment.setAttend(false);
    }


}
