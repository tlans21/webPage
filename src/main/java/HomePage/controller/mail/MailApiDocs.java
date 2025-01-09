package HomePage.controller.mail;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.MailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "Mail", description = "이메일 API")
public interface MailApiDocs {
    @Operation(summary = "이메일 보내기", description = "이메일에 일련번호를 전송하고 성공인지 실패인지 확인합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MailRequest.class),
                examples = @ExampleObject(
                    name = "기본 예시",
                    value = """
                        {
                            "email": "user@example.com"
                        }
                        """,
                    description = "이메일 전송을 위한 기본 요청 예시"
                )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전송 성공",
                    content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "statusCode": 200,
                                    "message": "전송 성공",
                                    "payload": {
                                        "success": true
                                    }
                                    
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "전송 실패",
                    content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "statusCode": 400,
                                    "message": "전송 실패",
                                    "payload": {
                                        "success": false
                                    }
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 접속 에러",
                    content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "statusCode": 500,
                                    "message": "서버 접속 에러",
                                    "payload": {
                                        "success": false
                                    }                       
                                }
                                """)
                    )
            )
        }
    )
    CommonResponse<Map<String, Object>> mailSend(
            @RequestBody @Valid MailRequest request);



    @Operation(summary = "이메일 일련번호 검증", description = "이메일에 전송된 일련번호를 검증합니다.")
    @ApiResponses({
             @ApiResponse(
                    responseCode = "200",
                    description = "전송 성공",
                    content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "statusCode": 200,
                                    "message": "전송 성공",
                                    "payload": {
                                        "success": true,
                                        "num": num
                                    }
                                    
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "전송 실패",
                    content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "statusCode": 400,
                                    "message": "전송 실패",
                                    "payload": {
                                        "success": false
                                    }
                                    
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 접속 에러",
                    content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "statusCode": 500,
                                    "message": "서버 접속 에러",
                                    "payload": {
                                        "success": false
                                    }
                                    
                                }
                                """)
                    )
            )
    })
    CommonResponse<Map<Object, Boolean>> mailCheck(@Parameter(description = "이메일")
                                                   @RequestParam String email,
                                                   @Parameter(description = "인증번호")
                                                   @RequestParam String userNumber);
}
