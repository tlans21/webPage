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
public class CommunityCommentDTO {
    private Long id;
    private Long userId;
    private Long boardId;
    private String writer;
    private String content;
    private Timestamp registerDate;
    private Timestamp updateDate;
    private Timestamp deleteDate;

    private int likeCount;
    private int dislikeCount;
    private Boolean userLikeStatus; // null: 좋아요/싫어요 없음, true: 좋아요, false: 싫어요

    private BoardInfo relatedBoard;

    public static class BoardInfo{
        private Long id;
        private String title;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
