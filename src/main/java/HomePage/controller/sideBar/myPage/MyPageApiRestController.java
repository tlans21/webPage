package HomePage.controller.sideBar.myPage;

import HomePage.common.response.CommonResponse;
import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.dto.MyPageRequest;
import HomePage.domain.model.entity.User;
import HomePage.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class MyPageApiRestController implements MyPageApiDocs{

    private UserService userService;
    @Autowired
    public MyPageApiRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/mypage-content/edit/profile")
    @Override
    public CommonResponse<Map<String, Boolean>> editMyProfile(@RequestBody @Valid MyPageRequest request, Authentication authentication){
        try {
            if (authentication == null){
                Map<String, Boolean> failResponse = new HashMap<>();
                failResponse.put("success", false);
                return CommonResponse.error(failResponse, "로그인이 필요합니다", HttpStatus.UNAUTHORIZED); // 401 클라이언트 인증이 되지않거나 유효하지 않음
            }

            //  요청으로 부터 변경하고자 하는 닉네임얻기
            String nickname = request.getNickname();

            if (nickname == null || nickname.trim().isEmpty()){
                Map<String, Boolean> failResponse = new HashMap<>();
                failResponse.put("success", false);
                return CommonResponse.error(failResponse, "닉네임은 필수 입력값입니다", HttpStatus.BAD_REQUEST); // 400에러
            }

            // authentication으로 유저 정보 얻기
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            User currentUser = principalDetails.getUser();


            User updatedUser = userService.updateUserNickname(currentUser, nickname);


            if (updatedUser.getNickname() == nickname) {
                Map<String, Boolean> successResponse = new HashMap<>();
                successResponse.put("success", true);
                return CommonResponse.success(successResponse, "프로필 수정 성공", HttpStatus.OK);
            } else {
                Map<String, Boolean> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                return CommonResponse.error(errorResponse, "프로필 수정 실패", HttpStatus.INTERNAL_SERVER_ERROR); // 백엔드 내부의 문제이므로 500 에러
            }
        } catch (AuthenticationException authenticationException){
            Map<String, Boolean> failResponse = new HashMap<>();
            failResponse.put("success", false);
            return CommonResponse.error(failResponse, "로그인 연결 문제", HttpStatus.UNAUTHORIZED); // 401 클라이언트 인증되지 않거나 유효한 인증 정보가 아님
        } catch(IllegalStateException e){
            Map<String, Boolean> failResponse = new HashMap<>();
            failResponse.put("success", false);
            return CommonResponse.error(failResponse,"잘못된 요청입니다.", HttpStatus.BAD_REQUEST); //  400 잘못된 요청
        } catch (Exception e){
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return CommonResponse.error(errorResponse,"서버 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR); // 서버 관련 500 에러
        }
    }
}
