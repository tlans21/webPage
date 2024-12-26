package HomePage.service;

import HomePage.domain.model.dto.RestaurantReviewCommentDTO;

import java.util.List;

public interface RestaurantReviewService {
    Long save(RestaurantReviewCommentDTO commentDTO);
    RestaurantReviewCommentDTO findById(Long id);
    List<RestaurantReviewCommentDTO> findByRestaurantIdWithoutJoin(Long restaurantId);
    List<RestaurantReviewCommentDTO> findByRestaurantIdWithJoin(Long restaurantId);
    void update(RestaurantReviewCommentDTO commentDTO);
    void delete(Long id);
    RestaurantReviewCommentDTO createReview(Long restaurantId, Long userId, String content, double rating);

}
