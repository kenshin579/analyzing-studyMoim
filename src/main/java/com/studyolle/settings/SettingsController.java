package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class SettingsController {
    private final AccountService accountService;
    static final String SETTING_PROFILE = "/settings/profile";

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
}
