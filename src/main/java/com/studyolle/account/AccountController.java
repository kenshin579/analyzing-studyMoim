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
        accountService.processSignUp(signUpForm);
        return "redirect:/";
    }

    @GetMapping("/checked-email")
    private String checkEmail(String token, String email, Model model){
         Account account = accountRepository.findByEmail(email);
        String resultPage = "account/checked-email";
        if(account == null){
             return resultPage;
         }
         if(!account.getEmailCheckToken().equals(token)){
             return resultPage;
         }

         account.setEmailVerified(true);
         account.setJoinedAt(LocalDateTime.now());

         model.addAttribute("nickname", account.getNickname());
         model.addAttribute("numberOfUser", accountRepository.count());
         return resultPage;
    }
}
