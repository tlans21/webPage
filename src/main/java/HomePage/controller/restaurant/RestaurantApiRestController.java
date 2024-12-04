package HomePage.controller.restaurant;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantSearchCriteria;
import HomePage.domain.model.entity.Page;
import HomePage.exception.ResourceNotFoundException;
import HomePage.service.naverApi.NaverApiMapService;
import HomePage.service.restaurant.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public CommonResponse<Map<String, Object>> getRestaurant(@RequestParam(value="page", defaultValue = "1") int page,
                                                             @RequestParam(value="sortOption", defaultValue = "평점순") String sortOption,
                                                             @RequestParam(value="themeOption", defaultValue = "") String themeOption,
                                                             @RequestParam(value="serviceOption", defaultValue = "") String serviceOption){

        try{
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

            return CommonResponse.success(response, "음식점 목록 조회 성공");
        } catch (ResourceNotFoundException e){
            return CommonResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return CommonResponse.error("서버 오류가 발생했습니다: " + e.getMessage(),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/restaurants/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer> createRestaurants(@RequestParam String query){
        System.out.println(query);
        try {
            int totalSaved = restaurantService.createRestaurants(query);
            return CommonResponse.success(totalSaved, "음식점이 성공적으로 생성되었습니다.");
        } catch (IllegalStateException e){
            return CommonResponse.error("잘못된 요청입니다: " + e.getMessage(),
                                                 HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return CommonResponse.error("서버 오류가 발생했습니다: " + e.getMessage(),
                                                  HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }






//    @PostMapping("/restaurants/create/image")
//    public ResponseEntity<List<Map<String, Restaurant>>> createRestaurantsImage(@RequestBody Map<String, List<Map<String, String>>> request) {
//        System.out.println("create");
//        List<Map<String, String>> restaurants = request.get("restaurants");
//        List<Map<String, Restaurant>> response = restaurantService.createRestaurantImage(restaurants);
//        return ResponseEntity.ok(response);
//    }

}
