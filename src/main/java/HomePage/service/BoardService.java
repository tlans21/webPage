package HomePage.service;

import HomePage.domain.model.entity.Board;
import HomePage.domain.model.entity.Page;

import java.util.List;

public interface BoardService <T extends Board>{
    Page<T> getBoardPage(int pageNumber);
    Page<T> getTopViewedBoardPage(int pageNumber);
    Page<T> getTopCommentCntBoardPage(int pageNumber);
    Page<T> getBoardPageBySearch(int pageNumber, String searchType, String searchKeyword);
    T getBoardById(Long id);
    Long saveBoard(T board);
    void updateBoard(T board);
    void deleteBoard(Long id);
    List<T> searchBoardsByTitle(String title);
    List<T> searchBoardsByWriter(String writer);
    List<T> getAllBoards();

    void incrementViews(Long id);

}
