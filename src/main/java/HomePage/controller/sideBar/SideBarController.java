package HomePage.controller.sideBar;

import HomePage.config.auth.PrincipalDetails;
import HomePage.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/v1/user")
public class SideBarController {
    private UserService userService;

    @Autowired
    public SideBarController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mypage")
    public String showMyPage(){
        return "myPage/myPageDetails";
    }

    @PutMapping("/deleteUser")
    public String softDeleteUser(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String username = principalDetails.getUsername();
        Long userId = principalDetails.getUser().getId();
        userService.deleteUser(userId);
        return "ss";
    }


}
