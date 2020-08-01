package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {
    //bean끼리만 bean을 주입받을 수 있다.
    //어떤 빈이 생성자가 하나만 있고,생성자가 받는 파라미터가 bean으로 등록이 되어 있다면,, spring4.2부터 자동으로 빈을 주입해주기 때문에
    // @Autowired나 @inject를 사용하지 않아도 의존성을 주입받을 수 있다.
     private final SignUpFormValidator signUpFormValidator;
     private final EmailLoginFormValidator emailLoginFormValidator;
     private final AccountService accountService;
     private final AccountRepository accountRepository;

     @InitBinder("signUpForm")
     public void initBinder(WebDataBinder webDataBinder){
         webDataBinder.addValidators(signUpFormValidator);
     }

     @InitBinder("emailLoginForm")
     public void setEmailLoginFormValidatorInitBinder(WebDataBinder webDataBinder) {webDataBinder.addValidators(emailLoginFormValidator);}

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

         accountService.completeSignUp(account);
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
         if(!account.canSendConfirmEmail()){
             model.addAttribute("oneHourError","재인증을 위한 이메일 요청은 1시간에 한 번만 전송할 수 있습니다.");
             model.addAttribute("email",account.getEmail());
             return "account/checked-email";
         }
         accountService.makeMailThenSend(account);
         return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account){
        Account byNickname = accountRepository.findByNickname(nickname);
        if(byNickname == null){
            throw new IllegalArgumentException("잘못된 접근방식입니다.");
        }
        model.addAttribute(byNickname);
        model.addAttribute("isOwner",account.equals(byNickname));
        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLogin(Model model){
         model.addAttribute("emailLoginForm",new EmailLoginForm());
         return "/email-login";
    }

    @PostMapping("/email-login")
    public String emailLogin(@Valid EmailLoginForm emailLoginForm, Errors errors, Model model, RedirectAttributes attributes){
         if(errors.hasErrors()){
             model.addAttribute("email");
             return "/email-login";
         }
        Account byEmail = accountRepository.findByEmail(emailLoginForm.getEmail());
        if(!byEmail.canSendConfirmEmail()){
            model.addAttribute("error","1시간 뒤 다시 요청해주세요.");
            return "/email-login";
        }
         accountService.sendMail(emailLoginForm.getEmail());
         attributes.addFlashAttribute("message","메일을 전송하였습니다.");
         return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String emailLogin(String email, String token, Model model){
         Account account = accountRepository.findByEmail(email);
         String resultPage = "/email-login";
         if(account == null || !account.isValidToken(token)){
             model.addAttribute("error","잘못된 접근방식입니다.");
             model.addAttribute("emailLoginForm", new EmailLoginForm());
             return resultPage;
         }
         accountService.emailLogin(account);
         return "/logged-in-email";
    }
}
