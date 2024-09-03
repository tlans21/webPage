package HomePage.admin.mapper;

import HomePage.domain.model.Board;
import HomePage.domain.model.CommunityBoard;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminBoardMapper {
    List<CommunityBoard> getAllBoards();
    CommunityBoard getBoardById(Long id);
    Long updateBoard(Board board);
    void deleteBoard(Long id);
}
