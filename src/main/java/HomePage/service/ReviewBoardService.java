package HomePage.service;

import HomePage.domain.model.Page;
import HomePage.domain.model.ReviewBoard;
import HomePage.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;


public class ReviewBoardService implements BoardService<ReviewBoard>{
    @Value("${reviewBoard.page-size}")
    int pageSize;

    private final BoardRepository<ReviewBoard> reviewBoardRepository;
    @Autowired
    public ReviewBoardService(BoardRepository<ReviewBoard> reviewBoardRepository) {
        this.reviewBoardRepository = reviewBoardRepository;
    }

    @Override
    public Page<ReviewBoard> getBoardPage(int pageNumber) {
        int totalBoards = reviewBoardRepository.count();
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);
        int offset = (pageNumber - 1) * pageSize;

        List<ReviewBoard> reviewBoards = reviewBoardRepository.findPage(offset, pageSize);
        return new Page<ReviewBoard>(reviewBoards, pageNumber, totalPages, pageSize);
    }

    @Override
    public ReviewBoard getBoardById(Long id) {
        return reviewBoardRepository.selectById(id)
                               .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
    }

    @Override
    public void saveBoard(ReviewBoard board) {
        reviewBoardRepository.save(board);
    }

    @Override
    public void updateBoard(ReviewBoard board) {
        if (!reviewBoardRepository.update(board)) {
            throw new RuntimeException("Failed to update board with id: " + board.getId());
        }
    }

    @Override
    public void deleteBoard(Long id) {
        ReviewBoard reviewBoard = getBoardById(id);
        if (!reviewBoardRepository.deleteById(reviewBoard.getId())) {
            throw new RuntimeException("Failed to delete board with id: " + id);
        }
    }

    @Override
    public List<ReviewBoard> searchBoardsByTitle(String title) {
        return reviewBoardRepository.selectByTitle(title)
                        .map(List::of)
                        .orElse(List.of());
    }

    @Override
    public List<ReviewBoard> searchBoardsByWriter(String writer) {
        return reviewBoardRepository.selectByWriter(writer)
                        .map(List::of)
                        .orElse(List.of());
    }

    @Override
    public List<ReviewBoard> getAllBoards() {
        return reviewBoardRepository.selectAll();
    }

    @Override
    public void incrementViewCount(Long boardId) {
        Optional<ReviewBoard> reviewBoardOpt = reviewBoardRepository.selectById(boardId);
        if (reviewBoardOpt.isPresent()) {
            ReviewBoard reviewBoard = reviewBoardOpt.get();
            reviewBoard.setViewCnt(reviewBoard.getViewCnt() + 1);
            reviewBoardRepository.update(reviewBoard);
        }
    }

    @Override
    public List<ReviewBoard> getTopViewedBoards(int limit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
