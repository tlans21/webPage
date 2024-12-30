package HomePage.domain.model.entity;


import HomePage.domain.model.enumData.LikeTargetType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Like {
    private Long id;
    private String targetType;  // R: 식당, B: 게시판
    private Long targetId;      // 식당 ID 또는 게시판 ID
    private Long userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isLike; // true: 좋아요, false: 싫어요
    // 팩토리 메서드
    public static Like createLike(LikeTargetType targetType, Long targetId, Long userId, Boolean isLike) {
        return Like.builder()
                .targetType(targetType.getCode())
                .targetId(targetId)
                .userId(userId)
                .isLike(isLike)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    //

    public void setLike(boolean like) {
        this.isLike = like;
    }
}
