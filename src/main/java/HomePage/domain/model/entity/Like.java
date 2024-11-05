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

    // 팩토리 메서드
    public static Like createLike(LikeTargetType targetType, Long targetId, Long userId) {
        return Like.builder()
                .targetType(targetType.getCode())
                .targetId(targetId)
                .userId(userId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}
