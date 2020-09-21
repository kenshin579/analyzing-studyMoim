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
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute("enrollments",event.getEnrollments());
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
        /*event.setTitle(eventForm.getTitle());
        event.setEventType(eventForm.getEventType());
        event.setLimitOfEnrollments(eventForm.getLimitOfEnrollments());
        event.setEndEnrollmentDateTime(eventForm.getEndEnrollmentDateTime());
        event.setStartDateTime(eventForm.getStartDateTime());
        event.setEndDateTime(eventForm.getEndDateTime());
        event.setDescription(eventForm.getDescription());*/
        eventService.updateEvent(event, eventForm);
        return "redirect:/study/"+path+"/events/"+event.getId();
    }
    @PostMapping("/events/{id}/remove")
    public String removeEvent(@PathVariable String path, @PathVariable Long id){
        eventService.removeEvent(id);
        return "redirect:/study/"+path+"/events";
    }


}
