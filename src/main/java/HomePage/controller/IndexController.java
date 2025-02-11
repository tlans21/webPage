package HomePage.controller;

import HomePage.config.interceptor.DynamicETag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class IndexController {

    @GetMapping("/")
    @DynamicETag(properties = {"authentication"})
    public String indexPost(HttpServletRequest request, HttpServletResponse response,
                            Authentication authentication, Model model) {

        boolean isAuthenticated = (authentication != null &&
                                        authentication.isAuthenticated() &&
                                        !(authentication.getPrincipal() instanceof String));

        model.addAttribute("authentication", isAuthenticated);
        return "index"; // 인덱스 페이지 뷰 이름
    }
    @GetMapping("/testRedirect")
    public void redirectPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }
}
