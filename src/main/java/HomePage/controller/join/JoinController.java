package HomePage.controller.join;

import HomePage.controller.UserForm;
import HomePage.domain.model.User;
import HomePage.exception.JoinException;
import HomePage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api")
public class JoinController {
    @Autowired
    private UserService userService;

    @PostMapping("/join")
    public RedirectView join(UserForm userForm) {
        try {
            User user = userService.userFormDTO(userForm);
            userService.join(user);
            System.out.println("회원가입에 성공");
            return new RedirectView("/loginForm");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new JoinException("Error joining user");
        }
    }
}
