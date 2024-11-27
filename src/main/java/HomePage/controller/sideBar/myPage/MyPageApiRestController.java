package HomePage.controller.sideBar.myPage;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.entity.User;
import HomePage.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class MyPageApiRestController {

    private UserService userService;
    @Autowired
    public MyPageApiRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/mypage-content/edit/profile")
    public ResponseEntity<Map<String, Boolean>> editMyProfile(@RequestBody Map<String, String> request, Authentication authentication){
        //  요청으로 부터 변경하고자 하는 닉네임얻기
        String nickname = request.get("nickname");
        System.out.println(nickname);
        // authentication으로 유저 정보 얻기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User currentUser = principalDetails.getUser();

        // principalDetails로 찾기
        User user = userService.updateUserNickname(currentUser, nickname);

        // 응답 주기
        Map<String, Boolean> response = new HashMap<>();

        if (user.getNickname() == nickname) {
            response.put("success", true);
        } else {
            response.put("success", false);
        }
        System.out.println(user.getNickname());
        return ResponseEntity.ok(response);
    }
}
