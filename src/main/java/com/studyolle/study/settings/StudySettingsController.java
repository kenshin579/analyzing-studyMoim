package com.studyolle.study.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.settings.form.TagForm;
import com.studyolle.settings.form.ZoneForm;
import com.studyolle.study.StudyService;
import com.studyolle.study.settings.Form.DescriptionForm;
import com.studyolle.tag.TagService;
import com.studyolle.zone.ZoneService;
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
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
@Controller
public class StudySettingsController {
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final StudyService studyService;
    private final TagService tagService;
    private final DescriptionFormValidator descriptionFormValidator;
    private final ZoneService zoneService;
    @InitBinder("descriptionForm")
    public void descriptionFormInitBinder(WebDataBinder webDataBinder){webDataBinder.addValidators(descriptionFormValidator);}

    @GetMapping("/description")
    public String studySetting(@CurrentUser Account account, @PathVariable String path, Model model){
        Study studyToUpdate = studyService.getStudyToUpdate(account, path);
        DescriptionForm descriptionForm = modelMapper.map(studyToUpdate, DescriptionForm.class);
        model.addAttribute("descriptionForm", descriptionForm);
        model.addAttribute(studyToUpdate);
        return "/study/settings/description";
    }

    @PostMapping("/description")
    public String updateDescription(@CurrentUser Account account, @Valid DescriptionForm descriptionForm, Errors errors, @PathVariable String path, RedirectAttributes attribute, Model model){
        Study studyToUpdate = studyService.getStudyToUpdate(account, path);
        if(errors.hasErrors()){
            model.addAttribute("descriptionForm", descriptionForm);
            model.addAttribute(studyToUpdate);
            return "/study/settings/description";
        }
        studyService.updateDescription(studyToUpdate, descriptionForm);
        attribute.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/study/"+path;
    }

    @GetMapping("/tags")
    public String settingsTags(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study studyToUpdate = studyService.getStudyToUpdate(account, path);
        List<String> allTags = tagService.getAllTag();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        model.addAttribute("study", studyToUpdate);
        model.addAttribute("studyTags",studyToUpdate.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        return "/study/settings/tags";
    }
    @ResponseBody
    @PostMapping("/tags/add")
    public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm){
        Study study = studyService.getStudyToUpdateTag(account, path);
        String tag =tagForm.getTagTitle();
        Tag isTag = tagService.isTagThere(tag);
        if(isTag == null){
            isTag = tagService.saveTag(tag);
        }
        studyService.addTag(study, isTag);
        return ResponseEntity.ok().build();
    }
    @ResponseBody
    @PostMapping("/tags/remove")
    public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm){
        Study study = studyService.getStudyToUpdateTag(account, path);
        String tag = tagForm.getTagTitle();
        Tag isTag = tagService.isTagThere(tag);
        if(isTag == null){
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTag(study, isTag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String settingsZone(@CurrentUser Account account, @PathVariable String path, Model model) throws IOException {
        model.addAttribute("whitelist",objectMapper.writeValueAsString(zoneService.getAllZones()));
        Study study = studyService.getStudyToUpdateZone(account, path);
        model.addAttribute("study",study);
        model.addAttribute("studyZones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
        return "/study/settings/zone";
    }
    @ResponseBody
    @PostMapping("/zones/add")
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm, Errors errors, Model model){
        Study study = studyService.getStudyToUpdate(account, path);
        Zone zone = zoneService.getZoneToUpdateStudy(zoneForm.getCityName(), zoneForm.getProvinceName());
        if(zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.addZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/zones/remove")
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm){
        Study study = studyService.getStudyToUpdate(account, path);
        Zone zone = zoneService.getZoneToUpdateStudy(zoneForm.getCityName(), zoneForm.getProvinceName());
        studyService.removeZone(study, zone);
        return ResponseEntity.ok().build();
    }
}
