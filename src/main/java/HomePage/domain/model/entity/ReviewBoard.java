package HomePage.domain.model.entity;

import HomePage.domain.model.entity.Board;

public class ReviewBoard extends Board {
    private String restaurantName;
    private int rating;

    public ReviewBoard() {

    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
