package HomePage.repository;

import HomePage.domain.model.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository<T extends Board>{
    List<T> findPage(int offset, int limit);
    List<T> findPageOrderByTopView(int offset, int limit);
    List<T> findPageOrderByTopCommentCnt(int offset, int limit);
    List<T> findPageByTitle(int offset, int limit, String title);
    List<T> findPageByWriter(int offset, int limit, String title);
    int count();
    int countByTitle(String title);
    int countByWriter(String writer);
    T save(T board);

    boolean update(T board);
    boolean deleteById(Long id);

    Optional<T> selectById(Long id);
    List<T> selectByTitle(String title);
    List<T> selectByWriter(String writer);

    List<T> selectAll();

    boolean incrementViews(Long id);
    boolean updateCommentCnt(Long id, int commentCnt);

    void setTableName(String tableName);
}
