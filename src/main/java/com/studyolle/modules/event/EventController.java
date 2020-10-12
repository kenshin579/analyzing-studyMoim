package com.studyolle.modules.event;

import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
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
        Event event = eventService.getEvent(id);
        eventService.removeEnrollments(event);
        eventService.removeEvent(event);
        return "redirect:/study/"+path+"/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String enrollEvent(@PathVariable String path, @PathVariable Long id, @CurrentUser Account account, RedirectAttributes attributes){
        Event event = eventService.getEvent(id);
        Study study = studyService.getStudyToEnroll(path);
        eventService.newEnrollment(event, account);
        attributes.addFlashAttribute("message", "모임에 참가되었습니다.");
        return "redirect:/study/"+study.getPath()+"/events/"+event.getId();
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEvent(@PathVariable String path, @PathVariable Long id, @CurrentUser Account account, RedirectAttributes attributes){
        Study study = studyService.getStudy(path);
        Event event = eventService.getEvent(id);
        eventService.removeEnrollment(event, account);
        attributes.addFlashAttribute("message", "모임 참가가 취소되었습니다.");
        return "redirect:/study/"+study.getPath()+"/events/"+event.getId();
    }

    @ResponseBody
    @PostMapping("/events/{id}/approveEnrollment")
    public ResponseEntity approveEnrollment(@PathVariable String path, @PathVariable Long id, @RequestBody EnrollForm enrollment){
        Study study = studyService.getStudy(path);
        Event event = eventService.getEvent(id);
        Enrollment enrollement = enrollmentService.getEnrollement(Long.valueOf(enrollment.getId()));
        eventService.acceptEnrollment(event, enrollement);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/events/{id}/cancelEnrollment")
    public ResponseEntity cancelEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, @RequestBody Enrollment enrollment){
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventService.getEvent(id);
        Enrollment enrollement = enrollmentService.getEnrollement(Long.valueOf(enrollment.getId()));
        eventService.rejectEnrollment(event, enrollement);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/events/{id}/enrollments/{enrollmentId}/checkin")
    public String checkin(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, @PathVariable Long enrollmentId){
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventService.getEvent(id);
        Enrollment enroll = enrollmentService.getEnrollement(enrollmentId);
        eventService.checkInEnrollment(enroll);
        return "redirect:/study/"+study.getPath()+"/events/"+event.getId();
    }

    @GetMapping("/events/{id}/enrollments/{enrollmentId}/checkout")
    public String checkout(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, @PathVariable Long enrollmentId){
        Study study = studyService.getStudyToEnroll(path);
        Event event = eventService.getEvent(id);
        Enrollment enroll = enrollmentService.getEnrollement(enrollmentId);
        eventService.checkOutEnrollment(enroll);
        return "redirect:/study/"+study.getPath()+"/events/"+event.getId();
    }
}
