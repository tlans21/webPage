package HomePage.service;

import HomePage.domain.model.entity.Comment;

import java.util.List;

public interface CommentService <T extends Comment> {
    void saveComment(T comment);
    void  saveCommentAndIncrementCommentCnt(T comment);

    void updateComment(T comment);
    void deleteCommentByCommentId(Long id);
    void deleteCommentByWriter(String writer);
    void deleteCommentsByBoardId(Long id);
    T getCommentByCommentId(Long CommentId);
    List<T> getCommentByBoardId(Long board_id);
    List<T> getAllComments();

    int countByBoardId(Long id);
    boolean incrementCommentCnt(Long id, int commentCnt);
}
