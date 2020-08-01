package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.settings.form.AccountForm;
import com.studyolle.settings.form.NotificationsForm;
import com.studyolle.settings.form.PasswordForm;
import com.studyolle.settings.form.ProfileForm;
import com.studyolle.settings.validator.AccountValidator;
import com.studyolle.settings.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class SettingsController {
    private final AccountService accountService;
    private final PasswordValidator passwordValidator;
    private final AccountValidator accountValidator;
    static final String SETTING_PROFILE = "/settings/profile";
    static final String SETTING_PASSWORD = "/settings/password";
    static final String SETTING_NOTIFICATIONS = "/settings/notifications";
    static final String SETTING_ACCOUNT = "/settings/account";
    static final String SETTING_DELETEACCOUNT = "/settings/deleteAccount";

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
}
