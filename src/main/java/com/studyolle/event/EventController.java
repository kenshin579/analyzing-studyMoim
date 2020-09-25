package com.studyolle.event;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.*;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/study/{path}")
@Controller
public class EventController {
    private final StudyService studyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;
    private final EnrollmentService enrollmentService;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable String path, @Valid EventForm eventForm, Errors errors, Model model){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }
        Event event = eventService.createEvent(study, modelMapper.map(eventForm, Event.class), account);
        return "redirect:/study/"+path+"/events/"+event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, Model model){
        Event event = eventRepository.findById(id).orElseThrow();
        List<String> nicknameList = event.getEnrollments().stream().map((Enrollment t) -> t.getAccount().getNickname()).collect(Collectors.toList());
        model.addAttribute("nickNameList", nicknameList);
        model.addAttribute("account", account);
        model.addAttribute(event);
        model.addAttribute("enrollments", event.getEnrollments());
        model.addAttribute(studyRepository.findByPath(path));
        return "event/view";
    }

    @GetMapping("/events/{id}/edit")
    public String eventEditView(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, Model model){
        Event event = eventService.getEvent(id);
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(event);
        EventForm eventForm = modelMapper.map(event, EventForm.class);
        model.addAttribute("eventForm", eventForm);
        return "event/edit";
    }

    @PostMapping("/events/{id}/edit")
    public String eventEditSubmit(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, @Valid EventForm eventForm, Errors errors, Model model){
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventService.getEvent(id);
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/edit";
        }
        eventService.updateEvent(event, eventForm);
        return "redirect:/study/"+path+"/events/"+event.getId();
    }
    @PostMapping("/events/{id}/remove")
    public String removeEvent(@PathVariable String path, @PathVariable Long id){
        eventService.removeEvent(id);
        return "redirect:/study/"+path+"/events";
    }

    @PostMapping("/events/{id}/join")
    public String joinEvent(@PathVariable String path, @PathVariable Long id, @CurrentUser Account account, Model model, RedirectAttributes attributes){
        Event event = eventService.getEvent(id);
        if(enrollmentService.alreadyEnroll(event, account)){
            model.addAttribute("message", "이미 참여를 신청한 계정입니다.");
            return "/study/"+path+"/events/"+id;
        }

        if(event.getEventType().equals(EventType.FCFS)){
            if(!eventService.statEnroll(id)){
                enrollmentService.addFCFSEnrollment(event, account, false);
                attributes.addFlashAttribute("message", "대기 번호 "+eventService.getWaitingNum(id)+"입니다.");
            }else{
                enrollmentService.addFCFSEnrollment(event, account, true);
            }
        }else{

        }
        return "redirect:/study/"+path+"/events/"+id;
    }

    @PostMapping("/events/{id}/cancel")
    public String cancelEvent(@PathVariable String path, @PathVariable Long id, @CurrentUser Account account, RedirectAttributes attributes){
        Event event = eventService.getEvent(id);
        List<Enrollment> enrollments = event.getEnrollments();
        enrollments.sort((a,b) -> a.getEnrolledAt().compareTo(b.getEnrolledAt()));
        Enrollment enrollment = enrollmentService.getEnrollmentId(event, account);
        enrollments.remove(enrollment);
        if(event.getEventType().equals(EventType.FCFS)){
            enrollmentService.removeFCFSEnrollment(enrollment);
            enrollmentService.updateEnrollStat(enrollments, event.getLimitOfEnrollments());
        }else{

        }
        attributes.addFlashAttribute("message", "모임 참가가 취소되었습니다.");
        return "redirect:/study/"+path+"/events/"+id;
    }
}
