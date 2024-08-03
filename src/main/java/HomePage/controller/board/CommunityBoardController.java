package HomePage.controller.board;

import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.Page;
import HomePage.service.CommunityBoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/community")
public class CommunityBoardController {

    private final CommunityBoardService boardService;

    public CommunityBoardController(CommunityBoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/list")
    public String showCommunityBoardList(@RequestParam(value="page", defaultValue="1") int page, Model model) {
        Page<CommunityBoard> boardPage = boardService.getBoardPage(page);
        model.addAttribute("boardPage", boardPage);

        int totalPages = boardPage.getTotalPages();
        int currentPage = boardPage.getCurrentPage();
        int visiblePages = 5;
        int start = Math.max(1, currentPage - (visiblePages / 2));
        int end = Math.min(start + visiblePages - 1, totalPages);

        if (end - start + 1 < visiblePages) {
            start = Math.max(1, end - visiblePages + 1);
        }

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "/board/communityBoardList";
    }
    @GetMapping("/writeForm")
    public String showWriteForm(){
        return "/board/communityBoardWriteForm";
    }

}
