package HomePage.domain.model;

public class CommunityCommentDTO extends Comment {
    private CommunityBoard relatedCommunityBoard;
    public CommunityCommentDTO() {

    }
    public CommunityBoard getRelatedCommunityBoard() {
        return relatedCommunityBoard;
    }

    public void setRelatedCommunityBoard(CommunityBoard relatedCommunityBoard) {
        this.relatedCommunityBoard = relatedCommunityBoard;
    }
}
