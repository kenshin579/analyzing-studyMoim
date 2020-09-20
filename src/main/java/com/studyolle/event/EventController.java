package com.studyolle.event;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow());
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
        if(event.getLimitOfEnrollments() < eventForm.getLimitOfEnrollments()){
            model.addAttribute(event);
            model.addAttribute(study);
            model.addAttribute("limitError","모집인원을 더 적게 수정할 수 없습니다.");
            return "event/edit";
        }
        if(errors.hasErrors()){
            errors.rejectValue("limitOfEnrollments","wrong.value","모임 신청 인원보다 적은 모집 인원으로 변경할 수 없습니다.");
        }
        event.setTitle(eventForm.getTitle());
        event.setEventType(eventForm.getEventType());
        event.setLimitOfEnrollments(eventForm.getLimitOfEnrollments());
        event.setEndEnrollmentDateTime(eventForm.getEndEnrollmentDateTime());
        event.setStartDateTime(eventForm.getStartDateTime());
        event.setEndDateTime(eventForm.getEndDateTime());
        event.setDescription(eventForm.getDescription());
        eventService.updateEvent(event);
        return "redirect:/study/"+path+"/events/"+event.getId();
    }
}
