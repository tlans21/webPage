package HomePage.controller.join;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.JoinCheckUsernameRequest;
import HomePage.domain.model.dto.MailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "Join", description = "회원가입 관련 API")
public interface JoinRestApiDocs {

   @Operation(summary = "사용자명 중복 체크",
             description = "회원가입시 사용자가 입력한 username의 사용 가능 여부를 체크합니다.")
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
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200",
                       description = "사용 가능한 username",
                       content = @Content(mediaType = "application/json",
                       examples = @ExampleObject(
                               value = """
                               {
                                 "data": {
                                   "success": true
                                 },
                                 "message": "사용 가능한 Username입니다",
                                 "status": 200
                               }
                               """
                           ))),
           @ApiResponse(responseCode = "409",
                       description = "이미 사용중인 username",
                       content = @Content(mediaType = "application/json",
                       examples = @ExampleObject(
                               value = """
                               {
                                 "data": {
                                   "success": false
                                 },
                                 "message": "이미 사용중인 Username입니다",
                                 "status": 409
                               }
                               """
                           ))),
           @ApiResponse(responseCode = "408",
                       description = "요청 처리 시간 초과",
                       content = @Content(mediaType = "application/json",
                       examples = @ExampleObject(
                               value = """
                               {
                                 "data": {
                                   "success": false
                                 },
                                 "message": "요청 처리 시간이 초과되었습니다",
                                 "status": 408
                               }
                               """
                           ))),
           @ApiResponse(responseCode = "500",
                       description = "서버 내부 오류",
                       content = @Content(mediaType = "application/json",
                       examples = @ExampleObject(
                               value = """
                               {
                                 "data": {
                                   "success": false
                                 },
                                 "message": "서버로 부터 오류가 발생했습니다",
                                 "status": 500
                               }
                               """
                           )))
   })
   CommonResponse<Map<String, Boolean>> checkUsername(@RequestBody @Valid JoinCheckUsernameRequest request);
}
