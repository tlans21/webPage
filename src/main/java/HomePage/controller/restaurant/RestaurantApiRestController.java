package HomePage.controller.restaurant;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantSearchCriteria;
import HomePage.domain.model.entity.Page;
import HomePage.exception.ResourceNotFoundException;
import HomePage.service.naverApi.NaverApiMapService;
import HomePage.service.restaurant.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Restaurant", description = "음식점 API")
@RestController
@RequestMapping("/api")
public class RestaurantApiRestController implements RestaurantApiDocs {

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
    @Override
    public CommonResponse<Map<String, Object>> getRestaurant(
            @Parameter(description = "페이지 번호 (기본값: 1)")
            @RequestParam(value="page", defaultValue = "1") int page,
            @Parameter(description = "정렬 옵션 (평점순/조회순/리뷰순, 기본값: 평점순)")
            @RequestParam(value="sortOption", defaultValue = "평점순") String sortOption,
            @Parameter(description = "테마 옵션")
            @RequestParam(value="themeOption", defaultValue = "") String themeOption,
            @Parameter(description = "서비스 옵션")
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

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("Page", restaurantsPage);
            successResponse.put("totalRestaurantCount", totalRestaurantCount);

            return CommonResponse.success(successResponse, "음식점 목록 조회 성공", HttpStatus.OK);
        } catch (ResourceNotFoundException e){
            Map<String, Object> failResponse = new HashMap<>();
            failResponse.put("success", false);
            return CommonResponse.error(failResponse,"해당 정보를 데이터베이스에서 찾을 수 없음", HttpStatus.NOT_FOUND);
        } catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return CommonResponse.error(errorResponse,"서버 오류가 발생했습니다: " + e.getMessage(),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/restaurants/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public CommonResponse<Map<String, Object>> createRestaurants(
            @Parameter(description = "서비스 옵션")
            @RequestParam String query){

        System.out.println(query);
        try {
            Map<String, Object> successResponse = new HashMap<>();
            int totalSaved = restaurantService.createRestaurants(query);
            successResponse.put("success", true);
            successResponse.put("totalSaved", totalSaved);
            return CommonResponse.success(successResponse, "음식점이 성공적으로 생성되었습니다.", HttpStatus.OK);
        } catch (IllegalStateException e){
            Map<String, Object> failResponse = new HashMap<>();
            failResponse.put("success", false);
            return CommonResponse.error(failResponse, "잘못된 요청입니다: " + e.getMessage(),
                                                 HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return CommonResponse.error(errorResponse, "서버 오류가 발생했습니다: " + e.getMessage(),
                                                  HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
