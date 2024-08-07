package HomePage.service;

import HomePage.domain.model.CommunityComment;
import HomePage.repository.CommentRepository;

import java.util.List;


public class CommunityCommentService implements CommentService<CommunityComment> {
    private final CommentRepository<CommunityComment> commentRepository;

    public CommunityCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }



    public int getCommentCntById(Long boardId){
        return commentRepository.countByBoardId(boardId);
    }
    @Override
    public void saveComment(CommunityComment comment) {
        commentRepository.save(comment);
    }


    @Override
    public void updateComment(CommunityComment comment) {
        if (!commentRepository.update(comment)) {
            throw new RuntimeException("Failed to update board with id: " + comment.getId());
        }
    }

    @Override
    public CommunityComment getCommentByCommentId(Long commentId) {
        return commentRepository.findCommentById(commentId)
                               .orElseThrow(() -> new RuntimeException("Board not found with id: " + commentId));
    }

    @Override
    public void deleteCommentByCommentId(Long id) {
        CommunityComment comment = getCommentByCommentId(id);
        if (!commentRepository.deleteByCommentId(comment.getId())) {
            throw new RuntimeException("Failed to delete board with comment_id: " + id);
        }
    }

    @Override
    public void deleteCommentsByBoardId(Long id) {
        if(!commentRepository.deleteByBoardId(id)){
            throw new RuntimeException("Failed to delete board with board_id: " + id);
        }
    }

    @Override
    public void deleteCommentByWriter(String writer) {
        if (!commentRepository.deleteByWriter(writer)){
            throw new RuntimeException("Failed to delete board with writer: " + writer);
        }
    }

    @Override
    public List<CommunityComment> getCommentByBoardId(Long id) {
        return commentRepository.selectById(id);
    }

    @Override
    public List<CommunityComment> getAllComments() {
        return commentRepository.selectAll();
    }
}
