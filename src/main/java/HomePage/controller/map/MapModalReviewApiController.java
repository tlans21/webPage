package HomePage.controller.map;

import HomePage.common.response.CommonResponse;
import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantReviewCommentDTO;
import HomePage.domain.model.entity.User;
import HomePage.exception.RestaurantNotFoundException;
import HomePage.service.RestaurantReviewServiceImpl;
import HomePage.service.restaurant.RestaurantService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
public class MapModalReviewApiController implements MapModalReviewApiDocs{
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

        RestaurantReviewCommentDTO review = restaurantReviewService.createReview(restaurantId, userId, content, rating); // 리뷰 생성

        // 최신 리뷰 목록 조회
//        List<RestaurantReviewCommentDTO> reviewDTOs = restaurantReviewService.findByRestaurantIdWithoutJoin(restaurantId);
        List<RestaurantReviewCommentDTO> reviewDTOs = restaurantReviewService.findByRestaurantIdWithJoin(restaurantId, userId);
        System.out.println(reviewDTOs);
        RestaurantDto restaurantDto = restaurantService.getRestaurantById(restaurantId);

        for (RestaurantReviewCommentDTO reviewDTO : reviewDTOs){
            System.out.println(reviewDTO.getUserLikeStatus());
        }

        model.addAttribute("restaurant", restaurantDto);
        model.addAttribute("comments", reviewDTOs);

        // 댓글 목록 부분만 반환
        return "map/commentsSection :: comments-section";
    }
    @GetMapping("/review/{restaurantId}")
    @ResponseBody
    @Override
    public CommonResponse<?> fetchReviews(
            @Parameter(description = "음식점 ID", required = true, example = "1")
            @PathVariable Long restaurantId,
            Authentication authentication){
        try{
            RestaurantDto restaurantDto = restaurantService.getRestaurantById(restaurantId); // 현재 데이터 베이스에 있는지 확인
            if (!restaurantDto.getId().equals(restaurantId)){
                System.out.println("restaurantId:" + restaurantId);
                System.out.println("restaurant get Id:" +restaurantDto.getId());
                Map<String, Object> errorResponse = new HashMap<>();
                return CommonResponse.error(errorResponse, "데이터 베이스와 일치하지 않음", HttpStatus.INTERNAL_SERVER_ERROR);
            }

//            List<RestaurantReviewCommentDTO> reviews = restaurantReviewService.findByRestaurantIdWithoutJoin(restaurantId);
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            Long userId = principalDetails.getUser().getId();
            List<RestaurantReviewCommentDTO> reviews = restaurantReviewService.findByRestaurantIdWithJoin(restaurantId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);

            return CommonResponse.success(response, "리뷰를 불러오는데 성공하였습니다", HttpStatus.OK);
        }catch (RestaurantNotFoundException e) {
            return CommonResponse.error("해당 음식점이 존재하지 않습니다", HttpStatus.NOT_FOUND);
        }catch (DataAccessException e) {
            return CommonResponse.error("데이터베이스 조회 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
            return CommonResponse.error("서버 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
