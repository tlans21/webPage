package HomePage.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class LikeDTO {
    private Long id;
    private Long targetId;  // 게시글이나 댓글의 ID, 리뷰 ID, 식당ID
    private String targetType;  // "BOARD" 또는 "COMMENT", "REVIEW", "RESTAURANT"
    private Long userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isLike; // true: 좋아요, false: 싫어요

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
