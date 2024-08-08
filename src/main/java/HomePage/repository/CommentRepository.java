package HomePage.repository;

import HomePage.domain.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository <T extends Comment> {
    T save(T comment);
    List<T> selectAll();
    boolean update(T comment);
    boolean deleteByWriter(String writer);
    boolean deleteByCommentId(Long id);
    boolean deleteByBoardId(Long id);
    List<T> selectById(Long id);
    Optional<T> findCommentById(Long commentId);
    int countByBoardId(Long boardId);
}
