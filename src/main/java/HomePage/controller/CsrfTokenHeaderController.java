package HomePage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CsrfTokenHeaderController {
    @GetMapping("/csrftokenheader")
    public String csrfTokenHeader(){
        return "fragments/csrfTokenHeader";
    }
}
