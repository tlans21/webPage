package HomePage.controller.map;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantReviewCommentDTO;
import HomePage.domain.model.entity.User;
import HomePage.service.RestaurantReviewServiceImpl;
import HomePage.service.restaurant.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/user")
public class MapModalReviewApiController {
    @Autowired
    RestaurantReviewServiceImpl restaurantReviewService;
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    TemplateEngine templateEngine;

    @PostMapping("/create/review/{restaurantId}")
    public String createReview(@PathVariable Long restaurantId,
                               @RequestParam String content,
                               Authentication authentication,
                               Model model) {
        System.out.println("create");
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            User user = principalDetails.getUser();
            Long userId = user.getId();

            RestaurantReviewCommentDTO review = restaurantReviewService.createReview(restaurantId, userId, content);

            // 최신 리뷰 목록 조회
            List<RestaurantReviewCommentDTO> reviews = restaurantReviewService.findByRestaurantId(restaurantId);

            RestaurantDto restaurantDto = restaurantService.getRestaurantById(restaurantId);


            model.addAttribute("restaurant", restaurantDto);
            model.addAttribute("comments", reviews);

            // 댓글 목록 부분만 반환
            return "/map/commentsList :: comment-list";
        } catch (Exception e) {
            // 에러 처리
            model.addAttribute("error", "리뷰 생성 중 오류가 발생했습니다: " + e.getMessage());
            return "/map/commentsList :: .error-message";
        }
    }
    @GetMapping("/review/{restaurantId}")
    @ResponseBody
    public ResponseEntity<?> fetchReviews(@PathVariable Long restaurantId){
        List<RestaurantReviewCommentDTO> reviews = restaurantReviewService.findByRestaurantId(restaurantId);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);

        return ResponseEntity.ok(response);
    }


}
