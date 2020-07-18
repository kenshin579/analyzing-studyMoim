package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {
    //bean끼리만 bean을 주입받을 수 있다.
    //어떤 빈이 생성자가 하나만 있고,생성자가 받는 파라미터가 bean으로 등록이 되어 있다면,, spring4.2부터 자동으로 빈을 주입해주기 때문에
    // @Autowired나 @inject를 사용하지 않아도 의존성을 주입받을 수 있다.
     private final SignUpFormValidator signUpFormValidator;
     private final AccountService accountService;
     private final AccountRepository accountRepository;

     @InitBinder("signUpForm")
     public void initBinder(WebDataBinder webDataBinder){
         webDataBinder.addValidators(signUpFormValidator);
     }

    @GetMapping("/sign-up")
    private String signUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    private String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }
        Account account = accountService.processSignUp(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    private String checkEmail(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String resultPage = "account/checked-email";
        if(account == null){
            model.addAttribute("error","wrong.email");
             return resultPage;
         }
         if(!account.isValidToken(token)){
             model.addAttribute("error","wrong.token");
             return resultPage;
         }

         account.CompleteSignUp();
         accountService.login(account);
         model.addAttribute("nickname", account.getNickname());
         model.addAttribute("numberOfUser", accountRepository.count());
         return resultPage;
    }

    @GetMapping("/recheck-email")
    public String checkEmail(@CurrentUser Account account, Model model){
         model.addAttribute("email", account.getEmail());
         model.addAttribute("nickname", account.getNickname());
         return "account/recheck-email";
    }

    @GetMapping("/request-emailValidateToken")
    public String resendEmail(@CurrentUser Account account, Model model){
         /*if(!account.canSendConfirmEmail()){
             model.addAttribute("oneHourError","재인증을 위한 이메일 요청은 1시간에 한 번만 전송할 수 있습니다.");
            model.addAttribute("email",account.getEmail());
            return "account/checked-email";
         }*/

         accountService.makeMailThenSend(account);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
         return "account/login";
    }
}
