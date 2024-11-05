package HomePage.domain.model.entity;

public class CommunityComment extends Comment {
    private Long boardId;

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

    public CommunityComment(){

    }
}
