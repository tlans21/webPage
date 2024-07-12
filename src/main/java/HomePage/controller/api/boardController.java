package HomePage.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class boardController {
    @GetMapping("/api/v1/user/board")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // USER 또는 ADMIN 역할이 필요함
    public String accessBoard(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        return "board";
    }
}
