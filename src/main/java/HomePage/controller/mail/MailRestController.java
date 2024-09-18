package HomePage.controller.mail;

import HomePage.service.MailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(email);
        System.out.println(userNumber);
        boolean isMatch = userNumber.equals(String.valueOf(number));
        return ResponseEntity.ok(isMatch);
    }
}