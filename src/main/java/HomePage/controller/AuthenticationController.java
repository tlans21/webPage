package HomePage.controller;

import HomePage.config.auth.PrincipalDetails;
import HomePage.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthenticationController {


    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthenticationController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @GetMapping("/loginForm")
    public String loginForm(){
        return "/login/loginForm";
    }

//    @PostMapping("/login")
//    public String login(UserForm userForm){
//        userService.authenticateMember(userForm.getEmail(), userForm.getPassword());
//        return "redirect:/";
//    }
    @GetMapping("/joinForm")
    public String createUserForm(){
        return "user/createUserForm";
    }



    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails : " + principalDetails.getUser().getUsername());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/userInfo")
    public @ResponseBody String userInfo(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터";
    }


    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){
        System.out.println("/test/lgoin =======================" );
        // PrincipalDetails principalDetails = authentication.getPrincipal()
        // authentication 의 getPrincipal 메소드를 통해서 principal 을 얻어온 다음 타입에 맞게 캐스팅.
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        System.out.println("authentication : " + principalDetails.getUser());
        System.out.println("userDetails : " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth2/login")
    public @ResponseBody String testOauth2Login(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("/test/oauth2/login =======================" );

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("authentication : " + oAuth2User.getAttributes());
        System.out.println("oauth2User : " + oauth.getAttributes());

        return "세션 정보 확인하기";
    }
    @GetMapping("/test23")
    public String test(){
        return "test";
    }


}
