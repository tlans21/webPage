package HomePage.mapper;

import HomePage.domain.model.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LikeMapper {
    // 좋아요 생성
    void insert(Like like);

    // 좋아요 삭제
    void delete(@Param("targetType") String targetType,
                @Param("targetId") Long targetId,
                @Param("userId") Long userId);

    // 좋아요 여부 확인
    boolean exists(@Param("targetType") String targetType,
                  @Param("targetId") Long targetId,
                  @Param("userId") Long userId);

    // 좋아요 수 조회
    int countLikes(@Param("targetType") String targetType,
                  @Param("targetId") Long targetId);

    // 사용자의 특정 타입 좋아요 목록 조회
    List<Like> findByUserIdAndType(@Param("userId") Long userId,
                                   @Param("targetType") String targetType);
}
