package HomePage.controller.board;

import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.CommunityComment;
import HomePage.domain.model.Page;
import HomePage.service.CommunityBoardService;
import HomePage.service.CommunityCommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/community")
public class CommunityBoardController {

    private final CommunityBoardService boardService;
    private final CommunityCommentService commentService;

    public CommunityBoardController(CommunityBoardService boardService, CommunityCommentService commentService) {
        this.boardService = boardService;
        this.commentService = commentService;
    }

    @GetMapping("/list")
    public String showCommunityBoardList(@RequestParam(value="page", defaultValue = "1") int page,
                                         @RequestParam(value="sort", defaultValue = "latest") String sort,
                                         Model model) {
        Page<CommunityBoard> boardPage;
        switch (sort) {
            case "popular":
                boardPage = boardService.getTopViewedBoardPage(page);
                break;
            case "comment":
                boardPage = boardService.getTopCommentCntBoardPage(page);
                break;
            case "latest":
            default:
                boardPage = boardService.getBoardPage(page);
                break;
        }
        addPaginationAttributes(model, boardPage, sort);

        return "/board/communityBoardList";
    }

    private void addPaginationAttributes(Model model, Page<CommunityBoard> boardPage, String sort){
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
    }

    @GetMapping("/writeForm")
    public String showWriteForm(){
        return "/board/communityBoardWriteForm";
    }


    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id,
                              @RequestParam(required = false) String topic,
                              @RequestParam(defaultValue = "1") int page,
                              Model model) {

        // 게시글 상세 정보 조회 로직
        CommunityBoard board = boardService.getBoardByIdAndIncrementViews(id);
        if (board == null){
            return "error/404";
        }

        List<CommunityComment> comments = commentService.getCommentByBoardId(id);// 게시글 id를 통해서 해당 게시글의 댓글들을 불러온다.
        int commentCnt = commentService.getCommentCntById(id); // 게시글 id를 통해서 해당 게시글의 댓글 수를 불러온다.


        Page<CommunityBoard> boardPage = boardService.getBoardPage(page); // 현재 board의 매핑되어 있는 page를 통해 boarPage를 불러온다.
        int totalPages = boardPage.getTotalPages();
        int currentPage = boardPage.getCurrentPage();
        int visiblePages = 5;
        int start = Math.max(1, currentPage - (visiblePages / 2));
        int end = Math.min(start + visiblePages - 1, totalPages);


        if (end - start + 1 < visiblePages) {
            start = Math.max(1, end - visiblePages + 1);
        }

        model.addAttribute("comments", comments);
        model.addAttribute("article", board);
        model.addAttribute("commentCnt", commentCnt);
        model.addAttribute("topic", topic);
        model.addAttribute("page", page);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        Timestamp timestamp = boardPage.getContent().stream().findAny().get().getRegisterDate();
        System.out.println(timestamp);
        return "/board/boardViewDetail";
    }


}
