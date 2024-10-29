package HomePage.service.like;

import HomePage.domain.model.dto.LikeRes;
import HomePage.domain.model.enumData.LikeTargetType;

public interface LikeService {
    LikeRes toggleLike(LikeTargetType targetType, Long targetId, Long userId);
    int getLikeCount(LikeTargetType targetType, Long targetId);


}
