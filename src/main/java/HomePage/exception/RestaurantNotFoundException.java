package HomePage.exception;

public class RestaurantNotFoundException extends RuntimeException{
    public RestaurantNotFoundException(){
        super("음식점을 찾을 수 없습니다.");
    }
    public RestaurantNotFoundException(String message) {
          super(message);
    }
    public RestaurantNotFoundException(Long restaurantId) {
          super("음식점을 찾을 수 없습니다. (ID: " + restaurantId + ")");
    }
    public RestaurantNotFoundException(String message, Throwable cause) {
          super(message, cause);
    }
}
