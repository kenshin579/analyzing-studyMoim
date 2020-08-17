package com.studyolle.study;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
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

@RequiredArgsConstructor
@Controller
public class StudyController {
    private final StudyService studyService;
    private final StudyFormValidator studyFormValidator;
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;
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
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));
        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String members(@PathVariable String path, Model model){
        model.addAttribute(studyRepository.findByPath(path));
        return "study/members";
    }

    @GetMapping("/study/{path}/settings/description")
    public String studySetting(@PathVariable String path, Model model){
        model.addAttribute(studyRepository.findByPath(path));
        return "study/settings";
    }
}
