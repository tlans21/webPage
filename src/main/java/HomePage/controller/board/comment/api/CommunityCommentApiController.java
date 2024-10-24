package HomePage.controller.board.comment.api;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.entity.CommunityComment;
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

    @PostMapping("/create/{id}")
    public String createComment(@PathVariable Long id,
                                @RequestParam String content,
                                @RequestParam(defaultValue = "1") int page,
                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 댓글 생성 로직
        CommunityComment comment = new CommunityComment();
        comment.setContent(content);
        comment.setWriter(principalDetails.getUsername());
        comment.setBoardId(id);

        commentService.saveCommentAndIncrementCommentCnt(comment);

        return "redirect:/community/" + id + "?topic=community&page=" + page;
    }


}