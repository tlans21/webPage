package HomePage.service.like;

import HomePage.domain.model.entity.Like;

public interface LikeService {

    public Like save(Like like);
    public void addLikeToBoard(Long boardId, Long userId);
    public void addLikeToComment(Long boardId, Long userId);
    public void addLikeToRestaurant(Long restaurantId, Long userId);
    public void addLikeToReview(Long reviewId, Long userId);
}
