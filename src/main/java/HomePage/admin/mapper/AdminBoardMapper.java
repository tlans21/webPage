package HomePage.admin.mapper;

import HomePage.domain.model.CommunityBoard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminBoardMapper {

    CommunityBoard save(CommunityBoard communityBoard);
    List<CommunityBoard> findPage(@Param("offset") int offset, @Param("limit") int limit);
    List<CommunityBoard> findPageOrderByTopView(@Param("offset") int offset, @Param("limit") int limit);
    List<CommunityBoard> findPageOrderByTopCommentCnt(@Param("offset") int offset, @Param("limit") int limit);
    List<CommunityBoard> findPageByTitle(@Param("offset") int offset, @Param("limit") int limit, @Param("title") String title);
    List<CommunityBoard> findPageByWriter(@Param("offset") int offset, @Param("limit") int limit, @Param("writer") String writer);
    int count();
    int countByTitle(String title);
    int countByWriter(String writer);
    boolean update(CommunityBoard communityBoard);
    boolean deleteById(Long id);
    Optional<CommunityBoard> selectById(Long id);
    List<CommunityBoard> selectByTitle(String title);
    List<CommunityBoard> selectByWriter(String writer);
    List<CommunityBoard> selectAll();
    boolean incrementViews(Long id);
    boolean updateCommentCnt(@Param("id") Long id, @Param("commentCnt") int commentCnt);

}
