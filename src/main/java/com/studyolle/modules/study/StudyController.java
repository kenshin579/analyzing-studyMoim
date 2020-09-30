package com.studyolle.modules.study;

import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.event.EventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class StudyController {
    private final StudyService studyService;
    private final EventService eventService;
    private final StudyFormValidator studyFormValidator;
    private final ModelMapper modelMapper;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String createStudy(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("studyForm",new StudyForm());
        return "/study/new-study";
    }

    @PostMapping("/new-study")
    public String createStudy(@CurrentUser Account account, Model model , @Valid StudyForm studyForm, Errors errors){
        if(errors.hasErrors()){
            model.addAttribute("error","부적절한 접근");
            return "study/new-study";
        }
        Study study = studyService.saveStudy(modelMapper.map(studyForm, Study.class), account);
        return "redirect:/study/"+ URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String view(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyService.getStudy(path);
        if(study == null){
            return "page404";
        }
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String members(@PathVariable String path, Model model){
        Study study = studyService.getStudy(path);
        model.addAttribute(study);
        return "study/members";
    }

    @GetMapping("/study/{path}/events")
    public String eventView(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyService.getStudy(path);
        model.addAttribute(study);
        Map<String, List<Event>> events = eventService.getEvents(study);
        model.addAttribute("newEvents", events.get("newEvents"));
        model.addAttribute("oldEvents", events.get("oldEvents"));
        return "study/events";
    }

    @PostMapping("/study/{path}/join")
    public String joinRequest(@CurrentUser Account account, @PathVariable String path){
        Study study = studyService.getStudy(path);
        studyService.addMember(study, account);
        return "redirect:/study/"+path+"/members";
    }

    @PostMapping("/study/{path}/leave")
    public String leaveRequest(@CurrentUser Account account, @PathVariable String path){
        Study study = studyService.getStudy(path);
        studyService.leaveMember(study, account);
        return "redirect:/study/"+path+"/members";
    }

}
