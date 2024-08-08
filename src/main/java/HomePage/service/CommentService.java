package HomePage.service;

import HomePage.domain.model.Comment;

import java.util.List;

public interface CommentService <T extends Comment> {
    void saveComment(T Comment);
    void updateComment(T Comment);
    void deleteCommentByCommentId(Long id);
    void deleteCommentByWriter(String writer);
    void deleteCommentsByBoardId(Long id);
    T getCommentByCommentId(Long CommentId);
    List<T> getCommentByBoardId(Long board_id);
    List<T> getAllComments();
}
