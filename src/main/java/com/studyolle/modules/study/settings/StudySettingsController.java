package com.studyolle.modules.study.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.account.form.TagForm;
import com.studyolle.modules.account.form.ZoneForm;
import com.studyolle.modules.study.StudyFormValidator;
import com.studyolle.modules.study.StudyService;
import com.studyolle.modules.study.settings.Form.DescriptionForm;
import com.studyolle.modules.tag.TagService;
import com.studyolle.modules.zone.ZoneService;
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
    private final StudyFormValidator studyFormValidator;
    private final ZoneService zoneService;

    @InitBinder("descriptionForm")
    public void descriptionFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(descriptionFormValidator);
    }

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/description")
    public String studySetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study studyToUpdate = studyService.getStudyToUpdate(account, path);
        DescriptionForm descriptionForm = modelMapper.map(studyToUpdate, DescriptionForm.class);
        model.addAttribute("descriptionForm", descriptionForm);
        model.addAttribute(studyToUpdate);
        return "/study/settings/description";
    }

    @PostMapping("/description")
    public String updateDescription(@CurrentUser Account account, @Valid DescriptionForm descriptionForm, Errors errors,
                                    @PathVariable String path, RedirectAttributes attribute, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        if (errors.hasErrors()) {
            model.addAttribute("descriptionForm", descriptionForm);
            model.addAttribute(study);
            return "/study/settings/description";
        }
        studyService.updateDescription(study, descriptionForm);
        attribute.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/study/" + path;
    }

    @GetMapping("/tags")
    public String settingsTags(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute("study", study);
        List<String> allTags = tagService.getAllTag().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        model.addAttribute("studyTags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        return "/study/settings/tags";
    }

    @ResponseBody
    @PostMapping("/tags/add")
    public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateTag(tagForm.getTagTitle());
        studyService.addTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/tags/remove")
    public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateTag(tagForm.getTagTitle());
        studyService.removeTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String settingsZone(@CurrentUser Account account, @PathVariable String path, Model model) throws IOException {
        model.addAttribute("whitelist", objectMapper.writeValueAsString(zoneService.getAllZones()));
        Study study = studyService.getStudyToUpdateZone(account, path);
        model.addAttribute("study", study);
        model.addAttribute("studyZones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
        return "/study/settings/zone";
    }

    @ResponseBody
    @PostMapping("/zones/add")
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        Zone zone = zoneService.getZoneToUpdateStudy(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.addZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/zones/remove")
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        Zone zone = zoneService.getZoneToUpdateStudy(zoneForm.getCityName(), zoneForm.getProvinceName());
        studyService.removeZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String settingStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute("study", study);
        model.addAttribute("account", account);
        return "/study/settings/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attribute) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.publishStudy(study);
        attribute.addFlashAttribute("message","공개로 전환했습니다.");
        return "redirect:/study/" +path+"/settings/study";
    }

    @PostMapping("/study/nonPublish")
    public String nonPublishStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attribute) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.nonPublished(study);
        attribute.addFlashAttribute("message","비공개로 전환했습니다.");
        return "redirect:/study/" + path+"/settings/study";
    }

    @PostMapping("/study/recruiting")
    public String recruiting(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.recruiting(study);
        attributes.addFlashAttribute("mojimMessage", "스터디의 모집이 시작되었습니다.");
        return "redirect:/study/" + path+"/settings/study";
    }

    @PostMapping("/study/stopRecruiting")
    public String stopRecruiting(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.stopRecruiting(study);
        attributes.addFlashAttribute("mojimMessage", "스터디의 모집이 종료되었습니다.");
        return "redirect:/study/" + path+"/settings/study";
    }

    @PostMapping("/study/updateStudyName")
    public String updateStudyName(@CurrentUser Account account, @PathVariable String path, String newTitle) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyName(study, newTitle);
        return "redirect:/study/" + path;
    }

    @PostMapping("/study/updateStudyPath")
    public String updateStudyPath(@CurrentUser Account account, @PathVariable String path, String newPath, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        if(!studyService.isValidPath(newPath)){
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("studyPathError","해당 스터디 경로는 이용할 수 없습니다.");
            return "/study/settings/study";
        }
        studyService.updateStudyPath(study, newPath);
        return "redirect:/study/" + newPath;
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable String path){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.removeStudy(study);
        return "redirect:/";
    }
}
