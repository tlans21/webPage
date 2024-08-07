package HomePage.controller.board.api;

import HomePage.config.auth.PrincipalDetails;
import HomePage.controller.board.form.CommunityBoardWriteForm;
import HomePage.domain.model.CommunityBoard;
import HomePage.service.CommunityBoardService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/community")
public class CommunityBoardApiController {
    private final CommunityBoardService boardService;

    public CommunityBoardApiController(CommunityBoardService boardService) {
        this.boardService = boardService;
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
        boardService.saveBoard(board);
        return "redirect:/community/list";
    }


}
