package HomePage.controller.restaurant;

import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantSearchCriteria;
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

    // RequestParam으로 받은 값을 DB용 값으로 변환
    private String convertSortOption(String sortOption) {
        return switch (sortOption) {
            case "평점순" -> "averageRating";
            case "조회순" -> "viewCount";
            case "리뷰순" -> "reviewCount";
            default -> "averageRating";
        };
    }

    @GetMapping("/foods")
    public ResponseEntity<Map<String, Object>> getRestaurant(@RequestParam(value="page", defaultValue = "1") int page,
                                                             @RequestParam(value="sortOption", defaultValue = "평점순") String sortOption,
                                                             @RequestParam(value="themeOption", defaultValue = "") String themeOption,
                                                             @RequestParam(value="serviceOption", defaultValue = "") String serviceOption){

        System.out.println("page:" + page);
        System.out.println("sortOption: " + sortOption);
        System.out.println("themeOption: " + themeOption);
        System.out.println("serviceOption: " + serviceOption);

        //DTO 생성
        RestaurantSearchCriteria searchCriteria = RestaurantSearchCriteria.builder()
                .page(page)
                .sortBy(convertSortOption(sortOption))
                .theme(themeOption)
                .service(serviceOption)
                .build();

        Page<RestaurantDto> restaurantsPage = restaurantService.getRestaurantsPageBySearchCriteria(searchCriteria);
        int totalRestaurantCount = restaurantService.getTotalRestaurantCount();
        Map<String, Object> response = new HashMap<>();
        response.put("Page", restaurantsPage);
        response.put("totalRestaurantCount", totalRestaurantCount);
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
