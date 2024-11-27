package HomePage.mapper;

import HomePage.domain.model.entity.RestaurantReviewComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantReviewCommentMapper {
    Long save(RestaurantReviewComment comment);

    Optional<RestaurantReviewComment> findById(Long id);

    List<RestaurantReviewComment> findByRestaurantId(Long restaurantId);

    void deleteById(Long id);

    int countByRestaurantId(Long restaurantId);

    Boolean updateRatingByRestaurantId(@Param("id") Long restaurantId,@Param("rating") double rating);
}
