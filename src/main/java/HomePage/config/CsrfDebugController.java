package HomePage.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CsrfDebugController {
    @GetMapping("/csrf-debug")
    public String csrfDebug(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        System.out.println("Server-side CSRF Token: " + (csrfToken != null ? csrfToken.getToken() : "NULL"));
        return "debug";
    }
}