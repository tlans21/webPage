package HomePage.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String indexPage(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        System.out.println("abc : " + header);
        return "index";
    }
}
