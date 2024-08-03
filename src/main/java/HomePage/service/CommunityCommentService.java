package HomePage.service;

import HomePage.domain.model.CommunityComment;
import HomePage.repository.CommentRepository;

import java.util.List;


public class CommunityCommentService implements CommentService<CommunityComment> {
    private final CommentRepository<CommunityComment> commentRepository;

    public CommunityCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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
    public void deleteComment(Long id) {
        CommunityComment comment = getCommentById(id);
        if (!commentRepository.deleteByWriter(comment.getWriter())) {
            throw new RuntimeException("Failed to delete board with id: " + id);
        }
    }

    @Override
    public CommunityComment getCommentById(Long id) {
        return commentRepository.selectById(id)
                               .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
    }

    @Override
    public List<CommunityComment> getAllComments() {
        return commentRepository.selectAll();
    }
}
