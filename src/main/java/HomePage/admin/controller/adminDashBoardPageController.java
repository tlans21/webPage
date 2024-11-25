package HomePage.admin.controller;

import HomePage.admin.service.AdminDashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/admin")
public class adminDashBoardPageController {

    private final AdminDashBoardService adminDashBoardService;
    @Autowired
    public adminDashBoardPageController(AdminDashBoardService adminDashBoardService) {
        this.adminDashBoardService = adminDashBoardService;
    }

    @GetMapping("/adminPage/dashBoard")
    public String showDashBoardPage(Model model){
        model.addAttribute("userStats", adminDashBoardService.getUserStats());
        model.addAttribute("postStats", adminDashBoardService.getPostStats());
        model.addAttribute("userChartData", adminDashBoardService.getUserChartData());
        model.addAttribute("postChartData", adminDashBoardService.getPostChartData());
        return "admin/adminDashBoardPage";
    }
}
