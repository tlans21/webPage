package HomePage.admin.controller;

import HomePage.domain.model.Page;
import HomePage.domain.model.User;
import HomePage.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/admin")
public class adminUserManagementController {
    private final UserService userService;

    public adminUserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/adminPage/user-management")
    public String showUserManagementPage(@RequestParam(value="page", defaultValue = "1") int page,
                                         @RequestParam(value="searchType", required = false) String searchType,
                                         @RequestParam(value="searchKeyword", required = false) String searchKeyword,
                                         Model model) {
        Page<User> usersPage;
        if (isValidSearch(searchType, searchKeyword)) {
            usersPage = userService.getUsersPageBySearch(page, searchType, searchKeyword);
        } else {
            usersPage = userService.getUsersPage(page);
        }

        addPaginationAttributes(model, usersPage, searchType, searchKeyword);
        return "/admin/userManagementPage";
    }

    private boolean isValidSearch(String searchType, String searchKeyword){
        return searchType != null && searchKeyword != null && !searchKeyword.trim().isEmpty();
    }
    private void addPaginationAttributes(Model model, Page<User> usersPage, String searchType, String searchKeyword){
        int totalPages = usersPage.getTotalPages();
        int currentPage = usersPage.getCurrentPage();
        int visiblePages = 5;
        int start = Math.max(1, currentPage - (visiblePages / 2));
        int end = Math.min(start + visiblePages - 1, totalPages);

        if (end - start + 1 < visiblePages) {
            start = Math.max(1, end - visiblePages + 1);
        }
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
    }
}
