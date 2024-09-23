package HomePage.controller;

import HomePage.domain.model.User;
import HomePage.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/user/userList")
    public String list(Model model){
        List<User> users = userService.findMembers();
        model.addAttribute("members", users);

        return "/user/userList";
    }
}
