package HomePage.admin.service;

import HomePage.admin.mapper.AdminBoardMapper;
import HomePage.domain.model.Board;
import HomePage.domain.model.CommunityBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdminBoardServiceImpl implements AdminBoardService{

    private final AdminBoardMapper adminBoardMapper;

    @Autowired
    public AdminBoardServiceImpl(AdminBoardMapper adminBoardMapper) {
       this.adminBoardMapper = adminBoardMapper;
    }
    @Override
    public List<CommunityBoard> getAllBoards() {
        return adminBoardMapper.getAllBoards();
    }

    @Override
    public CommunityBoard getBoardById(Long id) {
        return null;
    }

    @Override
    public void deleteBoard(Long id) {
        adminBoardMapper.deleteBoard(id);
    }

    @Override
    public Long updateBoard(Board board) {
        return adminBoardMapper.updateBoard(board);
    }

    @Override
    public Long saveBoard(Board board) {
        return null;
    }
}
