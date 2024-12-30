package HomePage.controller.like;

import HomePage.common.response.CommonResponse;
import HomePage.config.auth.PrincipalDetails;
import HomePage.service.like.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/v1/user")
@RestController
public class LikeApiRestController {
    @Autowired
    LikeService likeService;
    @PostMapping("/review/{reviewId}/{action}")
    public CommonResponse<?> toggleLike(
            @PathVariable Long reviewId,
            @PathVariable String action,
            Authentication authentication

    ){
        System.out.println(reviewId);
        System.out.println(action);

        try{
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            Long userId = principalDetails.getUser().getId();

            boolean isLike = "like".equals(action);

            Map<String, Integer> counts = likeService.toggleLike(reviewId, userId, isLike);
            System.out.println("test3");
            return CommonResponse.success(counts,
                (isLike ? "좋아요" : "싫어요") + " 처리가 완료되었습니다",
                HttpStatus.OK);
        } catch (Exception e) {
            return CommonResponse.error("처리 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

