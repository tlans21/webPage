package HomePage.controller.restaurant;

import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.entity.Page;
import HomePage.service.naverApi.NaverApiMapService;
import HomePage.service.restaurant.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RestaurantApiRestController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NaverApiMapService naverApiMapService;
    @GetMapping("/foods")
    public ResponseEntity<Map<String, Page<RestaurantDto>>> getRestaurant(@RequestParam(value="page", defaultValue = "1") int page){
        Page<RestaurantDto> restaurantsPage = restaurantService.getRestaurantsPage(page);
        Map<String, Page<RestaurantDto>> response = new HashMap<>();
        response.put("Page", restaurantsPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants/create")
    public String createRestaurants(@RequestParam String query){
        int totalSaved = restaurantService.createRestaurants(query);

        return "Total restaurants saved: " + totalSaved;
    }



//    @PostMapping("/restaurants/create/image")
//    public ResponseEntity<List<Map<String, Restaurant>>> createRestaurantsImage(@RequestBody Map<String, List<Map<String, String>>> request) {
//        System.out.println("create");
//        List<Map<String, String>> restaurants = request.get("restaurants");
//        List<Map<String, Restaurant>> response = restaurantService.createRestaurantImage(restaurants);
//        return ResponseEntity.ok(response);
//    }

}
