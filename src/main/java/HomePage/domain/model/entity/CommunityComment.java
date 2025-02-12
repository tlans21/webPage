package HomePage.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityComment extends Comment {
    private Long boardId;
    private Long userId;
    private int likeCount;
    private int dislikeCount;

    @Override
    public Long getParentId() {
        return getBoardId();
    }

    @Override
    public void setParentId(Long boardId) {
        setBoardId(boardId);
    }

    public Long getBoardId(){
        return boardId;
    }

    public void setBoardId(Long boardId){
        this.boardId = boardId;
    }

}
