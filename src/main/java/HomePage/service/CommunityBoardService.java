package HomePage.service;

import HomePage.domain.model.entity.CommunityBoard;
import HomePage.domain.model.entity.CommunityComment;
import HomePage.domain.model.entity.Page;
import HomePage.repository.BoardRepository;
import HomePage.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommunityBoardService implements BoardService<CommunityBoard>{

    @Value("${communityBoard.page-size}")
    private int pageSize = 10;

    private final BoardRepository<CommunityBoard> communityBoardRepository;
    private final CommentRepository<CommunityComment> communityCommentRepository;


    public CommunityBoardService(BoardRepository<CommunityBoard> communityBoardRepository, CommentRepository<CommunityComment> communityCommentRepository) {
        this.communityBoardRepository = communityBoardRepository;
        this.communityCommentRepository = communityCommentRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, String> validateCommunityForm(Errors errors){
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error: errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }
    @Override
    @Transactional
    public Page<CommunityBoard> getBoardPage(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }

        int totalBoards = communityBoardRepository.count();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalBoards / pageSize));

        if (pageNumber > totalPages) {
            pageNumber = totalPages;
        }

        int offset = (pageNumber - 1) * pageSize;
        List<CommunityBoard> communityBoards = communityBoardRepository.findPage(offset, pageSize);

        return new Page<>(communityBoards, pageNumber, totalPages, pageSize);
    }

    @Override
    @Transactional
    public Page<CommunityBoard> getTopViewedBoardPage(int pageNumber) {
        int totalBoards = communityBoardRepository.count();
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);
        int offset = (pageNumber - 1) * pageSize;
        List<CommunityBoard> communityBoards = communityBoardRepository.findPageOrderByTopView(offset, pageSize);

        return new Page<CommunityBoard>(communityBoards, pageNumber, totalPages, pageSize);
    }

    @Override
    @Transactional
    public Page<CommunityBoard> getTopCommentCntBoardPage(int pageNumber) {
        int totalBoards = communityBoardRepository.count();
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);
        int offset = (pageNumber - 1) * pageSize;
        List<CommunityBoard> communityBoards = communityBoardRepository.findPageOrderByTopCommentCnt(offset, pageSize);

        return new Page<CommunityBoard>(communityBoards, pageNumber, totalPages, pageSize);
    }
    @Override
    @Transactional
    public Page<CommunityBoard> getBoardPageBySearch(int pageNumber, String searchType, String searchKeyword) {
        List<CommunityBoard> communityBoards;
        int offset = (pageNumber - 1) * pageSize;
        int totalBoards;
        int totalPages;

        if (searchType.equals("title") && isSearchByTitle(searchType)) {
            totalBoards = communityBoardRepository.countByTitle(searchKeyword);
            communityBoards = communityBoardRepository.findPageByTitle(offset, pageSize, searchKeyword);
        } else if (searchType.equals("writer") && isSearchByWriter(searchKeyword)) {
            totalBoards = communityBoardRepository.countByWriter(searchKeyword);
            communityBoards = communityBoardRepository.findPageByWriter(offset, pageSize, searchKeyword);
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
        return communityBoardRepository.selectById(id)
                       .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
    }

    @Override
    @Transactional
    public Long saveBoard(CommunityBoard board) {
        try {
            CommunityBoard savedBoard = communityBoardRepository.save(board);
            return savedBoard.getId();
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    @Override
    @Transactional
    public void updateBoard(CommunityBoard board) {
        if (!communityBoardRepository.update(board)) {
            throw new RuntimeException("Failed to update board with id: " + board.getId());
        }
    }


    @Override
    @Transactional
    public void deleteBoard(Long id) {
        if(!communityCommentRepository.deleteByBoardId(id)){
            throw new RuntimeException("Failed to delete comment with board_id: " + id);
        }
        if (!communityBoardRepository.deleteById(id)) {
            throw new RuntimeException("Failed to delete board with board_id: " + id);
        }
    }


    @Override
    public List<CommunityBoard> searchBoardsByTitle(String title) {
        return communityBoardRepository.selectByTitle(title);
    }

    @Override
    public List<CommunityBoard> searchBoardsByWriter(String writer) {
        return communityBoardRepository.selectByWriter(writer);
    }

    @Override
    public List<CommunityBoard> getAllBoards() {
        return communityBoardRepository.selectAll();
    }


    @Transactional
    public CommunityBoard getBoardByIdAndIncrementViews(Long id){
        CommunityBoard board = getBoardById(id);
        incrementViews(id);
        board.setViewCnt(board.getViewCnt() + 1);
        return board;
    }

    @Override
    public void incrementViews(Long id){
        boolean rowsAffected = communityBoardRepository.incrementViews(id);
        if(rowsAffected != true){
            throw new IllegalStateException("Expected 1 row to be updated, but got ");
        }
    }

}
