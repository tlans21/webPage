package HomePage.service;

import HomePage.domain.model.Comment;

import java.util.List;

public interface CommentService <T extends Comment> {
    void saveComment(T Comment);
    void updateComment(T Comment);
    void deleteComment(Long id);
    T getCommentById(Long id);
    List<T> getAllComments();
}
