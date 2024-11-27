package HomePage.controller.join.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class JoinErrorController {

    @GetMapping("/join/error")
    public String joinErrorPage(){
        return "error/409";
    }
}
