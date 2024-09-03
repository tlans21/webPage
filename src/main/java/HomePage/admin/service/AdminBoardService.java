package HomePage.admin.service;

import HomePage.domain.model.Board;
import HomePage.domain.model.CommunityBoard;

import java.util.List;

public interface AdminBoardService {
    List<CommunityBoard> getAllBoards();
    CommunityBoard getBoardById(Long id);
    void deleteBoard(Long id);
    Long updateBoard(Board board);
    Long saveBoard(Board board);
}
