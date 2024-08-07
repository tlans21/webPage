package HomePage.service;

import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.CommunityComment;
import HomePage.domain.model.Page;
import HomePage.repository.BoardRepository;
import HomePage.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class CommunityBoardService implements BoardService<CommunityBoard>{

    @Value("${communityBoard.page-size}")
    int pageSize;

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
    public Page<CommunityBoard> getBoardPage(int pageNumber) {
        int totalBoards = communityBoardRepository.count();
        System.out.println("count()");
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);
        int offset = (pageNumber - 1) * pageSize;

        List<CommunityBoard> communityBoards = communityBoardRepository.findPage(offset, pageSize);
        System.out.println("count()2");
        return new Page<CommunityBoard>(communityBoards, pageNumber, totalPages, pageSize);
    }

    @Override
    public CommunityBoard getBoardById(Long id) {
        return communityBoardRepository.selectById(id)
                       .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
    }

    @Override
    @Transactional
    public void saveBoard(CommunityBoard board) {
        communityBoardRepository.save(board);
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
        return communityBoardRepository.selectByTitle(title)
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public List<CommunityBoard> searchBoardsByWriter(String writer) {
        return communityBoardRepository.selectByWriter(writer)
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public List<CommunityBoard> getAllBoards() {
        return communityBoardRepository.selectAll();
    }

    @Override
    public void incrementViewCount(Long boardId) {
        Optional<CommunityBoard> communityBoardOpt = communityBoardRepository.selectById(boardId);
        if (communityBoardOpt.isPresent()) {
            CommunityBoard communityBoard = communityBoardOpt.get();
            communityBoard.setViewCnt(communityBoard.getViewCnt() + 1);
            communityBoardRepository.update(communityBoard);
        }
    }

    @Override
    public List<CommunityBoard> getTopViewedBoards(int limit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
