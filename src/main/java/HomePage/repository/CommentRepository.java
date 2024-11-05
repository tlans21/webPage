package HomePage.repository;

import HomePage.domain.model.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository <T extends Comment> {
    T save(T comment);
    List<T> selectAll();

    int count();
    int countByCreatedAtAfter(LocalDateTime date);
    int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    boolean update(T comment);
    boolean deleteByWriter(String writer);
    boolean deleteByCommentId(Long id);
    boolean deleteByBoardId(Long id);
    List<T> selectByBoardId(Long id);
    List<T> selectByWriter(String writer);
    List<T> selectRecentByWriter(String writer, int limit);
    Optional<T> findCommentById(Long commentId);
    int countByBoardId(Long boardId);
    void setTableName(String tableName);
}
