package HomePage.controller.restaurant;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.RestaurantDto;
import HomePage.domain.model.dto.RestaurantSearchCriteria;
import HomePage.domain.model.entity.Page;
import HomePage.exception.ResourceNotFoundException;
import HomePage.service.naverApi.NaverApiMapService;
import HomePage.service.restaurant.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Restaurant", description = "음식점 API")
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
    @Operation(summary = "음식점 목록 조회", description = "페이지네이션, 정렬, 테마, 서비스 옵션에 따른 음식점 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                        {
                            "statusCode": 200,
                            "message": "음식점 목록 조회 성공",
                            "payload": {
                                "Page": {
                                    "content" : [
                                    {
                                        "id": "1",
                                        "title": "안동집 칼국수",
                                        "link": "string",
                                        "viewCnt": "int",
                                        "reviewCnt": "int",
                                        "category": "한식>칼국수, 만두",
                                        "address": "서울특별시 동대문구 제기동 1019 신관 지하1층",
                                        "roadAddress": "서울특별시 동대문구 고산자로36길 3 신관 지하1층",
                                        "homePageLink": null,
                                        "mapx": 1270391428,
                                        "mapy": 375790378,
                                        "imageUrl": "https://d12zq4w4guyljn.cloudfront.net/750_750_20230614140614_photo1_b045aed748f9.jpg",
                                        "averageRating": 5
                                    }]
                                },
                                "totalRestaurantCount": 1
                            }
                        }
                        """))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "음식점을 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 404,
                        "message": "음식점을 찾을 수 없습니다",
                        "payload": null
                    }
                    """))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 500,
                        "message": "서버 오류가 발생했습니다",
                        "payload": null
                    }
                    """))
        )
    })

    @GetMapping("/foods")
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

            Map<String, Object> response = new HashMap<>();
            response.put("Page", restaurantsPage);
            response.put("totalRestaurantCount", totalRestaurantCount);

            return CommonResponse.success(response, "음식점 목록 조회 성공", HttpStatus.OK);
        } catch (ResourceNotFoundException e){
            return CommonResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return CommonResponse.error("서버 오류가 발생했습니다: " + e.getMessage(),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "음식점 생성", description = "새로운 음식점을 생성합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 201,
                        "message": "음식점이 성공적으로 생성되었습니다.",
                        "payload": {
                            "totalSaved": 1
                        }
                    }
                    """))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "음식점 생성 실패",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 400,
                        "message": "잘못된 요청입니다.",
                        "payload": null
                    }
                    """))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 500,
                        "message": "서버 오류가 발생했습니다",
                        "payload": null
                    }
                    """))
        )
    })

    @GetMapping("/restaurants/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Map<String, Object>> createRestaurants(
            @Parameter(description = "서비스 옵션")
            @RequestParam String query){

        System.out.println(query);
        try {
            Map<String, Object> response = new HashMap<>();
            int totalSaved = restaurantService.createRestaurants(query);
            response.put("totalSaved", totalSaved);
            return CommonResponse.success(response, "음식점이 성공적으로 생성되었습니다.", HttpStatus.OK);
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
