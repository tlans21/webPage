package HomePage.controller.map;

import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantReviewCommentDTO;
import HomePage.service.RestaurantReviewServiceImpl;
import HomePage.service.restaurant.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class MapModalController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private RestaurantReviewServiceImpl restaurantReviewService;
    @PostMapping("/map/modal-map")
    public String showModalMap(@RequestBody Map<String, Long> payload, Model model) {
        Long id = payload.get("id"); // restaurant_id값
        boolean isSuccess = restaurantService.updateRestaurantViewCnt(id);// 조회수 업데이트
        System.out.println(isSuccess);
        RestaurantDto restaurantDto = restaurantService.getRestaurantById(id);
        List<RestaurantReviewCommentDTO> comments = restaurantReviewService.findByRestaurantId(id);

        model.addAttribute("restaurant", restaurantDto);
        model.addAttribute("comments",  comments);
        System.out.println("mapModal");
        System.out.println(id);
        return "/map/modal :: mapModal-content";
   }


    @GetMapping("/test/map")
    public String showTestMap(){
        return "/map/maptest";
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
