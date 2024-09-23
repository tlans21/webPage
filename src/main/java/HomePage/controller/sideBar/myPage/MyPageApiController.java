package HomePage.controller.sideBar.myPage;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.User;
import HomePage.service.user.UserMyPageService;
import HomePage.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/user")
public class MyPageApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMyPageService userMyPageService;


    @GetMapping("/mypage-content")
    public String getMyPageContent(Model model, Authentication authentication) {
        // 현재 로그인한 사용자 정보 가져오기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User currentUser = principalDetails.getUser();

        model.addAttribute("userProfile", userMyPageService.getUserProfile(currentUser.getId()));
        model.addAttribute("userActivities", userMyPageService.getUserActivities(currentUser.getId()));
        model.addAttribute("userStats", userMyPageService.getUserStats(currentUser.getId()));

        // 마이페이지 콘텐츠만 반환
        return "fragments/mypage-content :: myPageContent";
    }

    @GetMapping("/mypage-content/redirectShow")
    public String showMyPageEditPage(Model model, Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User currentUser = principalDetails.getUser();


//        model.addAttribute()
        return "fragments/mypage-editPage :: myPageEditPage";
    }


}
