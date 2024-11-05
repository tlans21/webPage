package HomePage.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantReviewCommentDTO {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private String writer;
    private String content;
    private Timestamp registerDate;
    private Timestamp updateDate;
    private Timestamp deleteDate;
    private double rating;
}
