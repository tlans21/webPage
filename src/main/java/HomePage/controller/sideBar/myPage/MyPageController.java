package HomePage.controller.sideBar.myPage;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.User;
import HomePage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/user")
public class MyPageController {
    @Autowired
    private UserService userService;


    @GetMapping("/mypage-content")
    public String getMyPageContent(Model model, Authentication authentication) {
        // 현재 로그인한 사용자 정보 가져오기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User currentUser = principalDetails.getUser();
        model.addAttribute("user", currentUser);

//        // 사용자 통계 정보 가져오기
//        UserStats stats = userStatsService.getUserStats(currentUser.getId());
//        model.addAttribute("userStats", stats);
//
//        // 최근 활동 정보 가져오기
//        List<Activity> recentActivities = activityService.getRecentActivities(currentUser.getId());
//        model.addAttribute("recentActivities", recentActivities);

        // 마이페이지 콘텐츠만 반환
        return "fragments/mypage-content :: myPageContent";
    }
}
