package HomePage.controller;

import HomePage.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {


    private final MemberService memberService;

    public AuthenticationController(MemberService memberService) {
        this.memberService = memberService;
    }



    @GetMapping("/login")
    public String loginPage(MemberForm memberForm){
        return "/login/login";
    }

    @PostMapping("/login/sign")
    public String signIn(MemberForm memberForm){
        memberService.authenticateMember(memberForm.getEmail(), memberForm.getPassword());
        return "redirect:/";
    }
}
