package HomePage.controller.board.api;

import HomePage.config.auth.PrincipalDetails;
import HomePage.controller.board.form.CommunityBoardWriteForm;
import HomePage.domain.model.CommunityBoard;
import HomePage.service.CommunityBoardService;
import HomePage.service.CommunityCommentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/community")
public class CommunityBoardApiController {
    private final CommunityBoardService boardService;
    private final CommunityCommentService commentService;

    public CommunityBoardApiController(CommunityBoardService boardService, CommunityCommentService commentService) {
        this.boardService = boardService;
        this.commentService = commentService;
    }

    @GetMapping("/write")
    public RedirectView RedirectWriteFormPage(){
        return new RedirectView("/community/writeForm");
    }

    @PostMapping("/submit")
    public String validateAndSubmitCommunityPost(@Valid CommunityBoardWriteForm boardWriteForm, Errors errors, Model model){
        if (errors.hasErrors()){
            model.addAttribute("communityBoardWriteForm", boardWriteForm);

            Map<String, String> validatorResult = boardService.validateCommunityForm(errors);
            for (String key : validatorResult.keySet()){
                model.addAttribute(key, validatorResult.get(key));
            }
            // 유효성 검사에 실패하여 페이지 폼으로 리턴
            return "/board/communityBoardWriteForm";
        }
        CommunityBoard board = new CommunityBoard();

        board.setTitle(boardWriteForm.getTitle());
        board.setContent(boardWriteForm.getContent());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(principalDetails.getUsername());
        board.setWriter(principalDetails.getUsername());
        board.setViewCnt(0);
        board.setCommentCnt(0);
        boardService.saveBoard(board);
        return "redirect:/community/list";
    }
    @DeleteMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String currentUsername = principalDetails.getUsername();

        CommunityBoard board = boardService.getBoardById(id);

        if (board == null){
            return "error/404";
        }

        if (!hasEditPermission(board, principalDetails)){
            return "error/403";
        }

        boardService.deleteBoard(id); // 게시글 삭제, 게시글 관련된 댓글들도 삭제

        return "redirect:/community/list";
    }


    @GetMapping("/{id}/editForm")
    public String showBoardEditForm(@PathVariable Long id, Model model, Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String currentUsername = principalDetails.getUsername();

        CommunityBoard board = boardService.getBoardById(id); // 게시판 정보 불러오기

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
    @PutMapping("/{id}/edit")
    public String editBoard(@Valid CommunityBoardWriteForm boardWriteForm,
                            Errors errors, @PathVariable Long id, Model model){
        // Error errors는 @Valid 매개변수 다음으로 와야합니다. 그렇지 않으면 정상적으로 동작하지 않습니다.
        CommunityBoard board = boardService.getBoardById(id);

        if (errors.hasErrors()){
           model.addAttribute("communityBoardWriteForm", boardWriteForm);
           model.addAttribute("article", board);

           Map<String, String> validatorResult = boardService.validateCommunityForm(errors);
           for (String key : validatorResult.keySet()){
               model.addAttribute(key, validatorResult.get(key));
           }
           // 유효성 검사에 실패하여 페이지 폼으로 리턴
           return "board/boardEditView";
        }

        board.setTitle(boardWriteForm.getTitle());
        board.setContent(boardWriteForm.getContent());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String currentUsername = principalDetails.getUsername();

        if (board == null){
            return "error/404";
        }

        // 해당 유저의 게시글이 맞는지 권한 체크
        if (!hasEditPermission(board, principalDetails)){
            return "error/403";
        }
        boardService.updateBoard(board);

        return "redirect:/community/list";
    }

    private boolean hasEditPermission(CommunityBoard board, PrincipalDetails principalDetails) {
          String currentUsername = principalDetails.getUsername();
          return board.getWriter().equals(currentUsername) || isAdmin(principalDetails);
    }

    private boolean isAdmin(PrincipalDetails principalDetails){
        return principalDetails.getUser().getRoles().contains("ADMIN");
    }

}
