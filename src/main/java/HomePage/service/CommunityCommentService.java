package HomePage.service;

import HomePage.domain.model.entity.CommunityBoard;
import HomePage.domain.model.entity.CommunityComment;
import HomePage.repository.BoardRepository;
import HomePage.repository.CommentRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public class CommunityCommentService implements CommentService<CommunityComment> {
    private final CommentRepository<CommunityComment> commentRepository;
    private final BoardRepository<CommunityBoard> boardRepository;

    public CommunityCommentService(CommentRepository commentRepository, BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
    }



    public int getCommentCntById(Long boardId){
        return commentRepository.countByBoardId(boardId);
    }

    @Override
    @Transactional
    public void saveCommentAndIncrementCommentCnt(CommunityComment comment) {
        saveComment(comment);
        int commentCnt = countByBoardId(comment.getBoard_id());
        incrementCommentCnt(comment.getBoard_id(), commentCnt);
    }

    @Override
    public void saveComment(CommunityComment comment){
        commentRepository.save(comment);
    }
    @Override
    public int countByBoardId(Long BoardId){
        return commentRepository.countByBoardId(BoardId);
    }
    @Override
    public boolean incrementCommentCnt(Long id, int commentCnt){
        return boardRepository.updateCommentCnt(id, commentCnt);
    }




    @Override
    @Transactional
    public void updateComment(CommunityComment comment) {
        if (!commentRepository.update(comment)) {
            throw new RuntimeException("Failed to update board with id: " + comment.getId());
        }
    }

    @Override
    @Transactional
    public CommunityComment getCommentByCommentId(Long commentId) {
        return commentRepository.findCommentById(commentId)
                               .orElseThrow(() -> new RuntimeException("Board not found with id: " + commentId));
    }

    @Override
    @Transactional
    public void deleteCommentByCommentId(Long id) {
        CommunityComment comment = getCommentByCommentId(id);
        if (!commentRepository.deleteByCommentId(comment.getId())) {
            throw new RuntimeException("Failed to delete board with comment_id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteCommentsByBoardId(Long id) {
        if(!commentRepository.deleteByBoardId(id)){
            throw new RuntimeException("Failed to delete board with board_id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteCommentByWriter(String writer) {
        if (!commentRepository.deleteByWriter(writer)){
            throw new RuntimeException("Failed to delete board with writer: " + writer);
        }
    }

    @Override
    public List<CommunityComment> getCommentByBoardId(Long id) {
        return commentRepository.selectByBoardId(id);
    }

    @Override
    public List<CommunityComment> getAllComments() {
        return commentRepository.selectAll();
    }
}
