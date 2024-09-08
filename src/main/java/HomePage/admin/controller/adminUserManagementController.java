package HomePage.admin.controller;

import HomePage.domain.model.Page;
import HomePage.domain.model.User;
import HomePage.service.UserService;
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
                                         @RequestParam(value="searchType") String searchType,
                                         @RequestParam(value="searchKeyword") String searchKeyword,
                                         Model model) {

        Page<User> usersPage;
        String searchKeywordByTitle = null;
        String searchKeywordByWriter = null;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if(searchType.equals("writer")) {
                searchKeywordByWriter = searchKeyword;
                System.out.println("test1");
                usersPage = userService.getUsersPageBySearch(page, searchType, searchKeywordByWriter);
            } else {
                searchKeywordByTitle = searchKeyword;
                System.out.println("test2");
                usersPage = userService.getUsersPageBySearch(page, searchType, searchKeywordByTitle);
            }
        } else {
            usersPage = userService.getUsersPage(page);
        }

        addPaginationAttributes(model, usersPage, searchType, searchKeyword);

        model.addAttribute("usersPage", usersPage);

        return "/admin/userManagementPage";
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
        model.addAttribute("boardPage", usersPage);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
    }
}
