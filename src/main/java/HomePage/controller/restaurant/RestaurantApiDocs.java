package HomePage.controller.restaurant;

import HomePage.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Tag(name = "Restaurant", description = "음식점 API")
public interface RestaurantApiDocs {
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
    CommonResponse<Map<String, Object>> getRestaurant(
            @Parameter(description = "페이지 번호 (기본값: 1)")
            @RequestParam(value="page", defaultValue = "1") int page,
            @Parameter(description = "정렬 옵션 (평점순/조회순/리뷰순, 기본값: 평점순)")
            @RequestParam(value="sortOption", defaultValue = "평점순") String sortOption,
            @Parameter(description = "테마 옵션")
            @RequestParam(value="themeOption", defaultValue = "") String themeOption,
            @Parameter(description = "서비스 옵션")
            @RequestParam(value="serviceOption", defaultValue = "") String serviceOption);


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
    @ResponseStatus(HttpStatus.CREATED)
    CommonResponse<Map<String, Object>> createRestaurants(
            @Parameter(description = "서비스 옵션")
            @RequestParam String query);
}
