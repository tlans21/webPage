package HomePage.controller;

import HomePage.domain.model.User;
import HomePage.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/login/s")
    public String login(UserForm userForm){
        userService.authenticateMember(userForm.getEmail(), userForm.getPassword());
        return "redirect:/";
    }
    @GetMapping("/joinForm")
    public String createUserForm(){
        return "user/createUserForm";
    }

    @PostMapping("/join")
    public String join(UserForm userForm){
        User user = new User();
        user.setEmail(userForm.getEmail());
        String rawPassword = userForm.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        System.out.println(encPassword);
        user.setUsername(userForm.getUsername());
        user.setRole("ROLE_USER");
        user.setPhoneNumber(userForm.getPhoneNumber());
        userService.join(user);

        return "redirect:/";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }
    @GetMapping("/user")
    public @ResponseBody String user(){
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

}
