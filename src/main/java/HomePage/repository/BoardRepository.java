package HomePage.repository;

import HomePage.domain.model.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository<T extends Board>{
    List<T> findPage(int offset, int limit);
    List<T> findPageOrderByTopView(int offset, int limit);
    List<T> findPageOrderByTopCommentCnt(int offset, int limit);
    int count();
    T save(T board);

    boolean update(T board);
    boolean deleteById(Long id);

    Optional<T> selectById(Long id);
    Optional<T> selectByTitle(String title);
    Optional<T> selectByWriter(String writer);

    List<T> selectAll();

    int incrementViews(Long id);
    int updateCommentCnt(Long id, int commentCnt);

    void setTableName(String tableName);
}
