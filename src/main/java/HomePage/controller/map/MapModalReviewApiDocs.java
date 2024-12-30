package HomePage.controller.map;

import HomePage.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Map Review", description = "지도 리뷰 API")
public interface MapModalReviewApiDocs {
    @Operation(summary = "음식점 리뷰 조회", description = "특정 음식점의 모든 리뷰를 조회합니다.")
    @ApiResponses({
          @ApiResponse(
              responseCode = "200",
              description = "리뷰 조회 성공",
              content = @Content(mediaType = "application/json",
                  examples = @ExampleObject(value = """
                      {
                          "statusCode": 200,
                          "message": "리뷰를 불러오는데 성공하였습니다",
                          "payload": {
                              "reviews": [
                                  {
                                      "content": "맛있어요",
                                      "rating": 4.5,
                                      "nickname": "홍길동",
                                      "createdAt": "2024-01-01T12:00:00"
                                  }
                              ]
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
                          "message": "해당 음식점이 존재하지 않습니다",
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

    @GetMapping("/review/{restaurantId}")
    @ResponseBody
    CommonResponse<?> fetchReviews(
            @Parameter(description = "음식점 ID", required = true, example = "1")
            @PathVariable Long restaurantId,
            Authentication authentication);
}
