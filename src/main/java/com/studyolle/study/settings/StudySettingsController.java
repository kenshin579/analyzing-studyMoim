package com.studyolle.study.settings;

import com.studyolle.study.settings.Form.DescriptionForm;
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

@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
@Controller
public class StudySettingsController {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final StudyService studyService;
    private final DescriptionFormValidator descriptionFormValidator;

    @InitBinder("descriptionForm")
    public void descriptionFormInitBinder(WebDataBinder webDataBinder){webDataBinder.addValidators(descriptionFormValidator);}

    @GetMapping("/description")
    public String studySetting(@PathVariable String path, Model model){
        DescriptionForm descriptionForm = modelMapper.map(studyRepository.findByPath(path), DescriptionForm.class);
        model.addAttribute("descriptionForm", descriptionForm);
        model.addAttribute(studyRepository.findByPath(path));
        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateDescription(@Valid DescriptionForm descriptionForm, Errors errors, @PathVariable String path, RedirectAttributes attribute, Model model){
        if(errors.hasErrors()){
            model.addAttribute("descriptionForm", descriptionForm);
            model.addAttribute(studyRepository.findByPath(path));
            return "/study/settings/description";
        }
        studyService.updateDescription(studyRepository.findByPath(path), descriptionForm);
        attribute.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/study/"+path;
    }
}
