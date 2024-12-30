package HomePage.controller.map;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantReviewCommentDTO;
import HomePage.service.RestaurantReviewServiceImpl;
import HomePage.service.restaurant.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class MapModalController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private RestaurantReviewServiceImpl restaurantReviewService;
    @PostMapping("/map/modal-map")
    public String showModalMap(@RequestBody Map<String, Long> payload, Model model, Authentication authentication) {
        Long restaurantId = payload.get("id"); // restaurant_id값
        boolean isSuccess = restaurantService.updateRestaurantViewCnt(restaurantId);// 조회수 업데이트
        System.out.println(isSuccess);
        RestaurantDto restaurantDTO = restaurantService.getRestaurantById(restaurantId); // 식당 가져오기

        List<RestaurantReviewCommentDTO> commentDTOs;

        if (authentication == null){
            commentDTOs = restaurantReviewService.findByRestaurantIdWithoutJoin(restaurantId);
        } else {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            Long userId = principalDetails.getUser().getId();
            commentDTOs = restaurantReviewService.findByRestaurantIdWithJoin(restaurantId, userId);
        }

        List<RestaurantReviewCommentDTO> collect = commentDTOs.stream().map(commentDTO -> {
            System.out.println(commentDTO.getLikeCount());
            System.out.println(commentDTO.getDislikeCount());
            return commentDTO;
        }
        ).collect(Collectors.toList());

        model.addAttribute("restaurant", restaurantDTO);
        model.addAttribute("comments",  commentDTOs);

        System.out.println("mapModal");
        System.out.println(restaurantId);
        return "map/modal :: mapModal-content";
   }


    @GetMapping("/test/map")
    public String showTestMap(){
        return "map/maptest";
    }
    private void restaurantDtoMappingToModelFromRequest(Map<String, Object> request, Model model){
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setTitle((String) request.get("title"));
        restaurantDto.setMapx(((Number) request.get("mapx")).intValue());
        restaurantDto.setMapy(((Number) request.get("mapy")).intValue());
        restaurantDto.setAddress((String) request.get("address"));

        model.addAttribute("restaurant", restaurantDto);
    }

}
