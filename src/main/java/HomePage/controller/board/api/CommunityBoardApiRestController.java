package HomePage.controller.board.api;

import HomePage.service.CommunityBoardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/community")
public class CommunityBoardApiRestController {
    private final CommunityBoardService boardService;

    public CommunityBoardApiRestController(CommunityBoardService boardService) {
        this.boardService = boardService;
    }


//    @PutMapping("/articles/{id}")
//    public ResponseEntity<?> editCommunityArticle(@PathVariable Long id, @RequestBody CommunityBoard board, Authentication authentication){
//        CommunityBoard article = boardService.getBoardById(id);
//        if (article == null){
//            return ResponseEntity.notFound().build();
//        }
//
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // 캐스팅
//        if (principalDetails.getUsername() ==
//    }

}
