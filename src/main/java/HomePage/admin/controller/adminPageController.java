package HomePage.admin.controller;

import HomePage.admin.service.AdminBoardServiceImpl;
import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.entity.CommunityBoard;
import HomePage.domain.model.entity.Page;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/api/v1/admin")
@NoArgsConstructor
public class adminPageController {
    @Autowired
    private AdminBoardServiceImpl adminBoardService;

    @GetMapping("/adminPage/boardList")
    public String showAdminBoardPage(@RequestParam(value="page", defaultValue = "1") int page,
                                @RequestParam(value="sort", defaultValue = "latest") String sort,
                                @RequestParam(value="searchType", required = false) String searchType,
                                @RequestParam(value="searchKeyword", required = false) String searchKeyword,
                                Model model){
        Page<CommunityBoard> boardPage;
        String searchKeywordByTitle = null;
        String searchKeywordByWriter = null;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if(searchType.equals("writer")) {
                searchKeywordByWriter = searchKeyword;
                System.out.println("test1");
                boardPage = adminBoardService.getBoardPageBySearch(page, searchType, searchKeywordByWriter);
            } else {
                searchKeywordByTitle = searchKeyword;
                System.out.println("test2");
                boardPage = adminBoardService.getBoardPageBySearch(page, searchType, searchKeywordByTitle);
            }
        } else {
            boardPage = adminBoardService.getBoardPage(page);
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
    @DeleteMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String currentUsername = principalDetails.getUsername();

        CommunityBoard board = adminBoardService.getBoardById(id);

        if (board == null){
            return "error/404";
        }

        if (!hasEditPermission(board, principalDetails)){
            return "error/403";
        }

        adminBoardService.deleteBoard(id); // 게시글 삭제, 게시글 관련된 댓글들도 삭제

        return "redirect:/api/v1/admin/adminPage/boardList";
    }

    @GetMapping("/{id}/editForm")
    public String showBoardEditForm(@PathVariable Long id, Model model, Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String currentUsername = principalDetails.getUsername();

        CommunityBoard board = adminBoardService.getBoardById(id); // 게시판 정보 불러오기

        // 게시판이 없는 경우 리턴
        if (board == null){
            return "error/404";
        }

        // 해당 유저의 게시글이 맞는지 권한 체크
        if (!hasEditPermission(board, principalDetails)){
            return "error/403";
        }

        model.addAttribute("article", board);

        return "/board/boardEditView";
    }

    private boolean hasEditPermission(CommunityBoard board, PrincipalDetails principalDetails) {
          String currentUsername = principalDetails.getUsername();
          return board.getWriter().equals(currentUsername) || isAdmin(principalDetails);
    }
    private boolean isAdmin(PrincipalDetails principalDetails){
        return principalDetails.getUser().getRoles().contains("ADMIN");
    }
}
