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

}
