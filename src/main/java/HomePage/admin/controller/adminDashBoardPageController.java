package HomePage.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/admin")
public class adminDashBoardPageController {

    @GetMapping("/adminPage/dashBoard")
    public String showDashBoardPage(){
        return "/admin/adminDashBoardPage";
    }
}
