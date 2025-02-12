package HomePage.mapper;

import HomePage.domain.model.dto.CommunityCommentDTO;
import HomePage.domain.model.entity.CommunityComment;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CommunityCommentMapper {
    void save(CommunityComment comment);

        int count();

        int countByCreatedAtAfter(LocalDateTime date);

        int countByBoardId(Long boardId);

        int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

        List<CommunityCommentDTO> selectAll();

        int update(CommunityComment comment);

        CommunityComment findCommentById(Long commentId);
        List<CommunityCommentDTO> selectByBoardId(@Param("boardId") Long boardId);
        // 수정된 메서드: userId 파라미터 추가
        List<CommunityCommentDTO> selectByBoardId(@Param("boardId") Long boardId, @Param("userId") Long userId);

        List<CommunityCommentDTO> selectByWriter(String writer);

        List<CommunityCommentDTO> selectRecentByWriter(@Param("writer") String writer, @Param("limit") int limit);

        int deleteByWriter(String writer);

        int deleteByCommentId(Long id);

        int deleteByBoardId(Long id);
}
