package HomePage.controller.mail;

import HomePage.service.MailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MailRestController {
    private final MailSendService mailSendService;
    private int number;

    @Autowired
    public MailRestController(MailSendService mailSendService) {
        this.mailSendService = mailSendService;
    }

    @PostMapping("/send-mail")
    public ResponseEntity<Map<String, Boolean>> mailSend(@RequestBody Map<String, String> request) {
        Map<String, Boolean> response = new HashMap<>();
        System.out.println("이메일 보냄");
        try {
            String email = request.get("email");
            System.out.println(email);
            number = mailSendService.sendMail(email);
            String num = String.valueOf(number);

            response.put("success", true);
//            response.put("number", num);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
//            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/mailCheck")
    public ResponseEntity<Boolean> mailCheck(@RequestParam String email, @RequestParam String userNumber) {
        System.out.println("mailCheck");
        try {
           // URL 디코딩
           String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString());
           String decodedUserNumber = URLDecoder.decode(userNumber, StandardCharsets.UTF_8.toString());
           System.out.println(decodedEmail);
           System.out.println(decodedUserNumber);
           String parseStringNumber = Integer.toString(number);
           boolean isMatch = decodedUserNumber.equals(parseStringNumber);
           return ResponseEntity.ok(isMatch);
       } catch (UnsupportedEncodingException e) {
           // 디코딩 과정에서 오류가 발생한 경우
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
       }
    }
}