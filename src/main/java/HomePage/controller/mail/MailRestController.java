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
    public CommonResponse<Map<String, Boolean>> mailSend(@RequestBody @Valid MailRequest request) {
        Map<String, Boolean> response = new HashMap<>();

        try {
            String email = request.getEmail();
            System.out.println(email);
            number = mailSendService.sendMail(email);
            String num = String.valueOf(number);

            response.put("success", true);
//            response.put("number", num);
            return CommonResponse.success(response, "전송 성공", HttpStatus.OK);

        } catch (mailSendingException e){
            return CommonResponse.error("전송 실패", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
//            response.put("error", e.getMessage());
            return CommonResponse.error("서버 접속 에러", HttpStatus.INTERNAL_SERVER_ERROR);
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

           Map<Object, Boolean> response = new HashMap<>();

           if (isMatch){
               response.put("success", true);
           }
           else {
               response.put("success", false);
           }

           return CommonResponse.success(response, "전송 성공", HttpStatus.OK); // 전송 성공 -> 500 상태 코드 반환
       } catch (UnsupportedEncodingException e) {
           // 디코딩 과정에서 오류가 발생한 경우
           return CommonResponse.error("서버가 요청을 이해할 수 없음", HttpStatus.BAD_REQUEST); // 디코딩 실패 -> Bad Request는 "서버가 요청을 이해할 수 없음"
       } catch (Exception e) {
            return CommonResponse.error("서버 접속 오류", HttpStatus.INTERNAL_SERVER_ERROR); // 서버 접속 오류 -> INTERNAL_SERVER_ERROR 서버 상태 연결 오류
        }
    }
}