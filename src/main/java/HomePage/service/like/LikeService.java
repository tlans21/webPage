package HomePage.service.like;

import java.util.Map;

public interface LikeService {

    public Map<String, Integer> toggleLikeFromReview(Long reviewId, Long userId, boolean isLike);
    public Map<String, Integer> toggleLikeFromComment(Long commentId, Long userId, boolean isLike);
}
