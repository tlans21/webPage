package HomePage.controller.sideBar.myPage;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.MyPageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;

import java.util.Map;

@Tag(name = "MyPage", description = "마이페이지 API")
public interface MyPageApiDocs {
    @Operation(summary = "프로필 수정", description = "사용자의 닉네임을 수정합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 200,
                        "message": "프로필 수정 성공",
                        "payload": {
                            "success": true
                        }
                    }
                    """))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 400,
                        "message": "잘못된 요청입니다",
                        "payload": {
                            "success": false
                        }
                    }
                    """))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "statusCode": 401,
                        "message": "로그인이 필요합니다",
                        "payload": {
                            "success": false
                        }
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
                        "message": "서버 오류가 발생하였습니다",
                        "payload": {
                            "success": false
                        }
                    }
                    """))
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MyPageRequest.class),
            examples = @ExampleObject(
                value = """
                    {
                        "nickname": "변경할 닉네임"
                    }
                    """,
                summary = "프로필 수정 요청 예시"
            )
        )
    )
    CommonResponse<Map<String, Boolean>> editMyProfile(
        @Parameter(hidden = true) MyPageRequest request,
        @Parameter(hidden = true) Authentication authentication
    );
}
