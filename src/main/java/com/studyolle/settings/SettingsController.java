package com.studyolle.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.form.*;
import com.studyolle.settings.validator.AccountValidator;
import com.studyolle.settings.validator.PasswordValidator;
import com.studyolle.tag.TagRepository;
import lombok.RequiredArgsConstructor;
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
@Controller
public class SettingsController {
    private final AccountService accountService;
    private final PasswordValidator passwordValidator;
    private final AccountValidator accountValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    static final String SETTING_PROFILE = "/settings/profile";
    static final String SETTING_PASSWORD = "/settings/password";
    static final String SETTING_NOTIFICATIONS = "/settings/notifications";
    static final String SETTING_ACCOUNT = "/settings/account";
    static final String SETTING_DELETEACCOUNT = "/settings/deleteAccount";
    static final String SETTING_TAG = "/settings/tag";

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){webDataBinder.addValidators(passwordValidator);}

    @InitBinder("accountForm")
    public void InitBinder(WebDataBinder webDataBinder){webDataBinder.addValidators(accountValidator);}

    @GetMapping(SETTING_PROFILE)
    public String settingProfile(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("profileForm", new ProfileForm(account));
        return SETTING_PROFILE;
    }

    @PostMapping(SETTING_PROFILE)
    public String updateProfile(@CurrentUser Account account, @Valid ProfileForm profileForm, Errors errors,
                                Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTING_PROFILE;
        }
        accountService.updateProfile(account, profileForm);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:"+SETTING_PROFILE;
    }

    @GetMapping(SETTING_PASSWORD)
    public String settingPassword(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("passwordForm", new PasswordForm());
        return SETTING_PASSWORD;
    }

    @PostMapping(SETTING_PASSWORD)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTING_PASSWORD;
        }
        accountService.updatePassword(account, passwordForm);
        attributes.addFlashAttribute("message","비밀번호가 변경되었습니다.");
        return "redirect:"+SETTING_PASSWORD;
    }

    @GetMapping(SETTING_NOTIFICATIONS)
    public String settingNotifications(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("notificationsForm",new NotificationsForm());
        return SETTING_NOTIFICATIONS;
    }

    @PostMapping(SETTING_NOTIFICATIONS)
    public String updateNotifications(@CurrentUser Account account, @Valid NotificationsForm notificationsForm, Errors errors,
                                      Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTING_NOTIFICATIONS;
        }
        accountService.updateNotifications(account, notificationsForm);
        attributes.addFlashAttribute("message","변경되었습니다.");
        return "redirect:" + SETTING_NOTIFICATIONS;
    }
    @GetMapping(SETTING_ACCOUNT)
    public String settingAccount(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("accountForm", new AccountForm(account.getNickname()));
        return SETTING_ACCOUNT;
    }

    @PostMapping(SETTING_ACCOUNT)
    public String updateAccount(@CurrentUser Account account, @Valid AccountForm accountForm, Errors errors, Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTING_ACCOUNT;
        }
        accountService.updateAccount(account, accountForm.getNewNickname());
        attributes.addFlashAttribute("message","닉네임이 변경되었습니다.");
        return "redirect:"+SETTING_ACCOUNT;
    }

    @PostMapping(SETTING_DELETEACCOUNT)
    public String deleteAccount(@CurrentUser Account account){
        accountService.deleteAccount(account);
        return "redirect:/";
    }

    @GetMapping(SETTING_TAG)
    public String settingTag(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        List<String> userTags = accountService.getTags(account).stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        model.addAttribute("userTags", userTags);
        return SETTING_TAG;
    }

    @PostMapping(SETTING_TAG+"/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String tags = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(tags);
        if(tag == null){
            tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
        }
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping(SETTING_TAG+"/remove")
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String removeTitle = tagForm.getTagTitle();
        Tag tags = tagRepository.findByTitle(removeTitle);
        if(tags == null){
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, removeTitle);
        return ResponseEntity.ok().build();
    }
}
