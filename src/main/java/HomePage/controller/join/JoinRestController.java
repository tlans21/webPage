package HomePage.controller.join;

import HomePage.domain.model.User;
import HomePage.exception.JoinRequestProcessingTimeoutException;
import HomePage.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/api")
public class JoinRestController {
    @Autowired
    UserService userService;
    @PostMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> request){
        String username = request.get("username");
        Map<String, Boolean> response = new HashMap<>();
        Optional<User> user;

        try{
            user = userService.findByUsername(username);
        } catch(Exception e){
            throw new JoinRequestProcessingTimeoutException("An error occurred while processing the request\", e");
        }

        if (!user.isPresent()){
            response.put("exists", false);
        }else {
            response.put("exists", true);
        }
        System.out.println("/check-username 실행");
        return ResponseEntity.ok(response);
   }
}
