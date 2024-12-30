package HomePage.mapper;

import HomePage.domain.model.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {
    // 좋아요 생성
    int insert(Like like);

    // 좋아요 갯수 리뷰 컬럼에 갱신
    int updateReviewCounts(Like like);
    // 좋아요 삭제
    int delete(@Param("targetType") String targetType,
                @Param("targetId") Long targetId,
                @Param("userId") Long userId);

    // 좋아요 여부 확인
    boolean exists(@Param("targetType") String targetType,
                  @Param("targetId") Long targetId,
                  @Param("userId") Long userId);

    // 좋아요 수 조회
    int countLikes(@Param("targetType") String targetType,
                   @Param("targetId") Long targetId,
                   @Param("isLike") boolean isLike
    );

    int update(Like like);

    Like findByTargetAndUser(@Param("targetType") String targetType,
                               @Param("targetId") Long targetId,
                               @Param("userId") Long userId);

}
