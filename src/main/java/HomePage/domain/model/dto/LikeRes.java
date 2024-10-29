package HomePage.domain.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeRes {
    private boolean isLiked;
    private int likeCount;
}
