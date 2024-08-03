package HomePage.controller.article;

import HomePage.domain.model.CommunityBoard;
import HomePage.service.CommunityBoardService;
import HomePage.service.CommunityCommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    private final CommunityBoardService boardService;
    private final CommunityCommentService commentService;

    public ArticleController(CommunityBoardService boardService, CommunityCommentService commentService) {
        this.boardService = boardService;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public String viewArticle(@PathVariable Long id,
                              @RequestParam(required = false) String topic,
                              @RequestParam(defaultValue = "1") int page,
                              Model model) {

        // 게시글 상세 정보 조회 로직
        CommunityBoard article = boardService.getBoardById(id);
        if (article == null){
            return "error/404";
        }



        model.addAttribute("article", article);
        model.addAttribute("topic", topic);
        model.addAttribute("page", page);
        // 모델에 게시글 정보 추가
        return "/articles/articleViewDetail";
    }
}