package HomePage.controller.map;

import HomePage.domain.model.dto.RestaurantDto;
import HomePage.service.restaurant.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class MapModalController {

    @Autowired
    private RestaurantService restaurantService;
    @PostMapping("/map/modal-map")
    public String showModalMap(@RequestBody Map<String, Long> payload, Model model) {
       Long id = payload.get("id");
       RestaurantDto restaurantDto = restaurantService.getRestaurantById(id);
       model.addAttribute("restaurant", restaurantDto);
       System.out.println("mapModal");
       System.out.println(id);
       return "/map/modal :: mapModal-content";
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
