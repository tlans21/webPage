package HomePage.service.like;

import java.util.Map;

public interface LikeService {

    public Map<String, Integer> toggleLike(Long reviewId, Long userId, boolean isLike);
}
