package HomePage.controller.article.api;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.CommunityComment;
import HomePage.service.CommunityCommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/comments")
public class CommunityCommentApiController {
    private final CommunityCommentService commentService;

    public CommunityCommentApiController(CommunityCommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create/{articleId}")
    public String createComment(@PathVariable Long articleId,
                                @RequestParam String content,
                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 댓글 생성 로직
        CommunityComment comment = new CommunityComment();
        comment.setContent(content);
        comment.setWriter(principalDetails.getUsername());
        comment.setBoard_id(articleId);
        System.out.println(principalDetails.getUsername());
        commentService.saveComment(comment);

        return "redirect:/articles/" + articleId;
    }
}