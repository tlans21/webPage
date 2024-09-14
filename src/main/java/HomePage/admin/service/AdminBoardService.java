package HomePage.admin.service;

import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.Page;

import java.util.List;

public interface AdminBoardService {
    Page<CommunityBoard> getBoardPage(int pageNumber);
    Page<CommunityBoard> getBoardPageBySearch(int pageNumber, String searchType, String searchKeyword);
    List<CommunityBoard> getAllBoards();
    CommunityBoard getBoardById(Long id);
    void deleteBoard(Long id);

    void updateBoard(CommunityBoard board);

    Long saveBoard(CommunityBoard board);
}
