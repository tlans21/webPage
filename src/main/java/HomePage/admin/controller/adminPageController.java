package HomePage.admin.controller;

import HomePage.admin.service.AdminBoardService;
import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.Page;
import HomePage.service.CommunityBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/api/v1/admin")
public class adminPageController {
    @Autowired
    private CommunityBoardService boardService;
    @Autowired
    private final AdminBoardService adminBoardService;

    public adminPageController(AdminBoardService adminBoardService) {
        this.adminBoardService = adminBoardService;
    }

    @GetMapping("/adminPage/boardList")
    public String showAdminBoardPage(@RequestParam(value="page", defaultValue = "1") int page,
                                @RequestParam(value="sort", defaultValue = "latest") String sort,
                                @RequestParam(value="searchType", required = false) String searchType,
                                @RequestParam(value="searchKeyword", required = false) String searchKeyword,
                                Model model){
        Page<CommunityBoard> boardPage = boardService.getBoardPage(page);
        String searchKeywordByTitle = null;
        String searchKeywordByWriter = null;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if(searchType.equals("writer")) {
                searchKeywordByWriter = searchKeyword;
                boardPage = boardService.getBoardPageBySearch(page, searchType, searchKeywordByWriter);
            } else {
                searchKeywordByTitle = searchKeyword;
                boardPage = boardService.getBoardPageBySearch(page, searchType, searchKeywordByTitle);
            }
        } else {
            switch (sort) {
                case "popular":
                    boardPage = boardService.getTopViewedBoardPage(page);
                    break;
                case "comments":
                    boardPage = boardService.getTopCommentCntBoardPage(page);
                    break;
                case "latest":
                default:
                    boardPage = boardService.getBoardPage(page);
                    break;
            }
        }

        addPaginationAttributes(model, boardPage, sort, searchType, searchKeyword);

        return "/admin/adminPage";
    }

    private void addPaginationAttributes(Model model, Page<CommunityBoard> boardPage, String sort, String searchType, String searchKeyword){
        int totalPages = boardPage.getTotalPages();
        int currentPage = boardPage.getCurrentPage();
        int visiblePages = 5;
        int start = Math.max(1, currentPage - (visiblePages / 2));
        int end = Math.min(start + visiblePages - 1, totalPages);

        if (end - start + 1 < visiblePages) {
            start = Math.max(1, end - visiblePages + 1);
        }
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("sort", sort);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
    }

    @GetMapping("/getAllBoards")
    public String getAllBoards(Model model){
        List<CommunityBoard> allBoards = adminBoardService.getAllBoards();
        model.addAttribute("allBoards", allBoards);

        for (CommunityBoard board : allBoards){
            System.out.println(board.getId());
        }



        return "/admin/allBoards";
    }
}
