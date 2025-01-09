package HomePage.controller.mail;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.MailRequest;
import HomePage.exception.mailSendingException;
import HomePage.service.MailSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MailRestController implements MailApiDocs{
    private final MailSendService mailSendService;
    private int number;

    @Autowired
    public MailRestController(MailSendService mailSendService) {
        this.mailSendService = mailSendService;
    }

    @Override
    @PostMapping("/send-mail")
    public CommonResponse<Map<String, Object>> mailSend(@RequestBody @Valid MailRequest request) {
        try {
            String email = request.getEmail();
            System.out.println(email);
            number = mailSendService.sendMail(email);
            String num = String.valueOf(number);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("num", num);
            successResponse.put("success", true);
            return CommonResponse.success(successResponse, "전송 성공", HttpStatus.OK);
        } catch (mailSendingException e){
            Map<String, Object> failResponse = new HashMap<>();
            failResponse.put("success", false);
            return CommonResponse.error(failResponse,"전송 실패", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return CommonResponse.error(errorResponse,"서버 접속 에러", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Operation(summary = "이메일 일련번호 검증", description = "이메일에 전송된 일련번호를 검증합니다.")
    @GetMapping("/mailCheck")
    public CommonResponse<Map<Object, Boolean>> mailCheck(
            @Parameter(description = "이메일")
            @RequestParam String email,
            @Parameter(description = "인증번호")
            @RequestParam String userNumber) {
        try {
           // URL 디코딩
           String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString());
           String decodedUserNumber = URLDecoder.decode(userNumber, StandardCharsets.UTF_8.toString());
           String parseStringNumber = Integer.toString(number);

           boolean isMatch = decodedUserNumber.equals(parseStringNumber);

           if (isMatch){
               Map<Object, Boolean> successResponse = new HashMap<>();
               successResponse.put("success", true);
               return CommonResponse.success(successResponse, "전송 성공", HttpStatus.OK); // 전송 성공 -> 200 상태 코드 반환
           }
           else {
               Map<Object, Boolean> failResponse = new HashMap<>();
               failResponse.put("success", false);
               return CommonResponse.success(failResponse, "전송 성공", HttpStatus.OK); // 전송 성공 -> 200 상태 코드 반환
           }
       } catch (UnsupportedEncodingException e) {
           // 디코딩 과정에서 오류가 발생한 경우
           return CommonResponse.error("서버가 요청을 이해할 수 없음", HttpStatus.BAD_REQUEST); // 디코딩 실패 -> Bad Request는 "서버가 요청을 이해할 수 없음"
       } catch (Exception e) {
            return CommonResponse.error("서버 접속 오류", HttpStatus.INTERNAL_SERVER_ERROR); // 서버 접속 오류 -> INTERNAL_SERVER_ERROR 서버 상태 연결 오류
        }
    }
}