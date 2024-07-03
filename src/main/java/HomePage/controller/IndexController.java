package HomePage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String indexPost(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        return "index"; // 인덱스 페이지 뷰 이름
    }
}

