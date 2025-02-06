package HomePage.controller;

import HomePage.domain.model.entity.User;
import HomePage.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/check-user")
    public ResponseEntity<Map<String, Boolean>> checkUser(@RequestBody Map<String, String> request){
        String username = request.get("username");
        String rawPassword = request.get("password");
        Map<String, Boolean> response = new HashMap<>();

        Optional<User> user = userService.authenticateMember(username, rawPassword);

        if (!user.isPresent()){
            response.put("exists", false);
        }else{
            response.put("exists", true);
        }
        System.out.println("/api/check-user 실행");
        return ResponseEntity.ok(response);
    }
}
