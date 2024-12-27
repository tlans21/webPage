package HomePage.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantReviewComment extends Comment{
    private Long restaurantId;
    private Long userId;

    @Override
    public Long getParentId() {
        return getRestaurantId();
    }

    @Override
    public void setParentId(Long boardId) {
        setRestaurantId(boardId);
    }

    public Long getRestaurantId(){
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId){
        this.restaurantId = restaurantId;
    }

    private double rating;

    private int likeCount;
    private int dislikeCount;
}
