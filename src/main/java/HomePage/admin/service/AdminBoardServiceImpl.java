package HomePage.admin.service;

import HomePage.admin.mapper.AdminBoardMapper;
import HomePage.admin.mapper.AdminCommentMapper;
import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class AdminBoardServiceImpl implements AdminBoardService{
    @Value("${communityBoard.page-size}")
    private int pageSize;
    private final AdminBoardMapper adminBoardMapper;
    private final AdminCommentMapper adminCommentMapper;
    @Autowired
    public AdminBoardServiceImpl(AdminBoardMapper adminBoardMapper, AdminCommentMapper adminCommentMapper) {
       this.adminBoardMapper = adminBoardMapper;
       this.adminCommentMapper = adminCommentMapper;
    }

    @Override
    public Page<CommunityBoard> getBoardPage(int pageNumber) {
        if (pageNumber <= 0){
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        int totalBoards = adminBoardMapper.count();
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);

        if (pageNumber > totalPages) {
            pageNumber = totalPages; // or throw an exception if you prefer
        }

        int offset = (pageNumber - 1) * pageSize;
        List<CommunityBoard> communityBoards = adminBoardMapper.findPage(offset, pageSize);

        return new Page<>(communityBoards, pageNumber, totalPages, pageSize);
    }

    @Override
    @Transactional
    public Page<CommunityBoard> getBoardPageBySearch(int pageNumber, String searchType, String searchKeyword) {
        List<CommunityBoard> communityBoards;
        int offset = (pageNumber - 1) * pageSize;
        int totalBoards;
        int totalPages;

        if (searchType.equals("title") && isSearchByTitle(searchType)) {
            totalBoards = adminBoardMapper.countByTitle(searchKeyword);
            communityBoards = adminBoardMapper.findPageByTitle(offset, pageSize, searchKeyword);
        } else if (searchType.equals("writer") && isSearchByWriter(searchKeyword)) {
            totalBoards = adminBoardMapper.countByWriter(searchKeyword);
            communityBoards = adminBoardMapper.findPageByWriter(offset, pageSize, searchKeyword);
        } else {
            return getBoardPage(pageNumber); // 실제로 수행되면 안되는 코드
        }
        totalPages = Math.max(1, (int) Math.ceil((double) totalBoards / pageSize));
        return new Page<CommunityBoard>(communityBoards, pageNumber, totalPages, pageSize);
    }
    private boolean isSearchByTitle(String title){
            return !isNullOrEmpty(title);
    }
    private boolean isSearchByWriter(String writer){
        return !isNullOrEmpty(writer);
    }

    private boolean isNullOrEmpty(String str){
        return str == null || str.trim().isEmpty();
    }

    @Override
    @Transactional
    public CommunityBoard getBoardById(Long id) {
        return adminBoardMapper.selectById(id)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        adminCommentMapper.deleteAllByBoardId(id);
        adminBoardMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateBoard(CommunityBoard board) {
        adminBoardMapper.update(board);
    }

    @Override
    @Transactional
    public Long saveBoard(CommunityBoard board) {
        return null;
    }

    @Override
    public List<CommunityBoard> getAllBoards() {
        return null;
    }

}
