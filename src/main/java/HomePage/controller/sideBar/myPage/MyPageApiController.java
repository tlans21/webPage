package HomePage.controller.sideBar.myPage;

import HomePage.config.auth.PrincipalDetails;
import HomePage.config.cache.viewResolver.ViewCacheManager;
import HomePage.domain.model.entity.User;
import HomePage.service.user.UserMyPageService;
import HomePage.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1/user")
public class MyPageApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMyPageService userMyPageService;
    @Autowired
    private ViewCacheManager cacheManager;

    @GetMapping("/mypage-content")
    public String showMyPageContent(Model model, Authentication authentication) {
        // 현재 로그인한 사용자 정보 가져오기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User currentUser = principalDetails.getUser();

        String cacheKey = "myPage_" + currentUser.getId(); // 사용자 id에 해당하는 캐시 값 생성

        Optional<String> cachedContent = cacheManager.getCache(cacheKey);
        if (cachedContent.isPresent()) {
            return cachedContent.get();
        }


        model.addAttribute("userProfile", userMyPageService.getUserProfile(currentUser.getId()));
        model.addAttribute("userActivities", userMyPageService.getUserActivities(currentUser.getId()));
        model.addAttribute("userStats", userMyPageService.getUserStats(currentUser.getId()));

        // 마이페이지 콘텐츠만 반환
        return "fragments/mypage-content :: myPageContent";
    }
}
