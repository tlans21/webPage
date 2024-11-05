package HomePage.mapper;

import HomePage.domain.model.dto.RestaurantSearchCriteria;
import HomePage.domain.model.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RestaurantMapper {
    void insert(Restaurant restaurant);
    Restaurant selectById(Long id);
    List<Restaurant> selectAll();
    void update(Restaurant restaurant);
    void delete(Long id);

    int count();
    List<Restaurant> findRestaurantPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    Restaurant selectByTitleAndAddress(@Param("title") String title, @Param("address") String address);

    Restaurant findRestaurantByTitle(String title);

    Restaurant selectByImageUrl(String imageUrl);

    void updateCountReview(@Param("restaurantId") Long restaurantId, @Param("reviewCnt") int reviewCnt);

    boolean updateViewCnt(Long restaurantId);
    void updateAverageRatingById(@Param("restaurantId") Long restaurantId, @Param("averageRating") double averageRating);
    int countRestaurantsBySearchCriteria(RestaurantSearchCriteria criteria);
    List<Restaurant> findRestaurantsBySearchCriteria(RestaurantSearchCriteria criteria);
}

