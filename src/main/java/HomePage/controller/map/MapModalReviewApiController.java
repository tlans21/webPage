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
                               @RequestParam double rating,
                               Authentication authentication,
                               Model model) {
        System.out.println("create");
        System.out.println(restaurantId);
        System.out.println(content);
        System.out.println(rating);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        Long userId = user.getId();

        RestaurantReviewCommentDTO review = restaurantReviewService.createReview(restaurantId, userId, content, rating);

        // 최신 리뷰 목록 조회
        List<RestaurantReviewCommentDTO> reviewDTOs = restaurantReviewService.findByRestaurantId(restaurantId);
        System.out.println(reviewDTOs);
        RestaurantDto restaurantDto = restaurantService.getRestaurantById(restaurantId);

        for (RestaurantReviewCommentDTO reviewDTO : reviewDTOs){
            System.out.println(reviewDTO.getContent());
        }

        model.addAttribute("restaurant", restaurantDto);
        model.addAttribute("comments", reviewDTOs);

        // 댓글 목록 부분만 반환
        return "map/commentsSection :: comments-section";

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
